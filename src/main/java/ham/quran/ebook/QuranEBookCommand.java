package ham.quran.ebook;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ham.quran.ebook.model.Page;
import ham.quran.ebook.model.PageIterator;
import ham.quran.ebook.model.Quran;
import ham.quran.ebook.resource.EBook;
import ham.quran.ebook.resource.TocBuilder;
import ham.quran.ebook.resource.TranslationReader;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.SneakyThrows;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubWriter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@QuarkusMain
@Command(name = "quranebook", mixinStandardHelpOptions = true)
public class QuranEBookCommand implements Runnable, QuarkusApplication {
    @Option(names = {"-q", "--quran"}, description = "Quran text dowloaded from https://tanzil.net/download/. Expected format with aya numbers")
    File quran;
    @Option(names = {"-t", "--translation"}, description = "Quran tranlation dowloaded from https://tanzil.net/trans/. Expected format with aya numbers")
    File translation;
    @Option(names = {"-m", "--meta"}, description = "Quran meta data dowloaded from https://tanzil.net/docs/Quran_Metadata. Expected format is xml")
    File meta;
    @Option(names = {"-tmp", "--template"}, description = "Page template", defaultValue="resources/page.st")
    File template;
    @Inject
    private BookConfiguration bookConfiguration;
    @Inject
    EBook ebook;

    @Inject
    CommandLine.IFactory factory;

    @Override
    @SneakyThrows
    public void run() {
        Log.infov("Quran text [{0}], translation [{1}], meta [{2}] ", quran, translation, meta);
        ClassLoader classLoader = getClass().getClassLoader();
        File metaFile = meta != null && meta.exists() ? meta : new File(classLoader.getResource("data/meta.xml").getPath());
        File quranFile = quran != null && quran.exists() ? quran : new File(classLoader.getResource("data/quran.txt").getPath());
        File translationFile = translation != null && translation.exists() ? translation : new File(classLoader.getResource("data/translation.txt").getPath());

        Book book = getEpubBook();
        var quranMeta = new XmlMapper().readValue(metaFile, Quran.class);
        var tocBuilder = new TocBuilder();
        var tranlationReader = new TranslationReader(quranFile, translationFile, quranMeta);
        var pageIterator = new PageIterator(quranMeta);
        Page prevPage = null;
        Map<Integer, Resource> resources = new TreeMap<>();
        for (int i = 1; i <= 604 && pageIterator.hasNext(); i++) {
            var currentPage = pageIterator.next();
            if(prevPage != null){
                tranlationReader.read(prevPage, currentPage);
                var pageString = ebook.createPage(prevPage);
                tocBuilder.process(prevPage);
                Log.debugv("Page [{0}]", pageString);
                Log.infov("Page [{0}]", i);
                var resource = new Resource(pageString.getBytes(), "page_" + prevPage.getIndex() + ".html");
                resources.put(prevPage.getIndex(), resource);
                book.addResource(resource);
            }
            prevPage = currentPage;
        }

        var toc = tocBuilder.getToc();
        toc.getNavigations().forEach(navigation -> {
            var tocReference = new TOCReference("Juz " + navigation.getJuz().getIndex(),resources.get(navigation.getPage()));
            book.getTableOfContents().addTOCReference(
                    tocReference);
            navigation.getChildren().forEach(subnav -> {
                tocReference.addChildSection(
                        new TOCReference( subnav.getSura().getIndex() + ". " + subnav.getSura().getEname(), resources.get(subnav.getPage())));
            });
        });

        book.generateSpineFromTableOfContents();

        EpubWriter epubWriter = new EpubWriter();

        epubWriter.write(book, new FileOutputStream("quran.epub"));
    }

    private Book getEpubBook() throws IOException {
        //create epub book
        Book book = new Book();
        var metadata = book.getMetadata();
        metadata.setAuthors(List.of(new Author(bookConfiguration.authorName(), bookConfiguration.authorSurname())));
        book.setCoverImage(getResource("data/cover.png", "cover.png"));
        book.getResources().add(getResource("styles/style.css", "style.css"));
        return book;
    }

    private static InputStream getResource(String path ) {
        ClassLoader classLoader = QuranEBookCommand.class.getClassLoader();
        return classLoader.getResourceAsStream( path );
    }

    private static Resource getResource(String path, String href ) throws IOException {
        return new Resource( getResource( path ), href );
    }
    public static void main(String... args) {
        Quarkus.run(QuranEBookCommand.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
