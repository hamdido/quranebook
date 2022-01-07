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
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubWriter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@QuarkusMain
@Command(name = "quranebook", mixinStandardHelpOptions = true)
public class QuranEBookCommand implements Runnable, QuarkusApplication {
    @Option(names = {"-d", "--directory"}, description = "Directory of the resources", defaultValue = "./src/main/resources")
    String resources;
    @Option(names = {"-l", "--lang"}, description = "Language code - e.g. BG", defaultValue = "BG")
    String lang;
    @Inject
    private BookConfiguration bookConfiguration;
    @Inject
    EBook ebook;

    @Inject
    CommandLine.IFactory factory;

    @Override
    @SneakyThrows
    public void run() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (!new File(resources).exists()){
            throw new FileNotFoundException("Missing location: " + resources);
        }

        File metaFile = new File(resources + "/data/meta.xml");
        File quranFile = new File(resources + "/data/quran.txt");
        File translationFile = new File(resources + "/data/translation_"+ lang.toLowerCase() + ".txt");

        if(!translationFile.exists()) {
            throw new FileNotFoundException("Missing translation: " + translationFile.getAbsolutePath());
        }

        Log.infov("Generation epub book for language[{0}] [{1}], translation [{2}], meta [{3}] ", lang, quranFile.getName(), translationFile.getName(), metaFile.getName());
        Book book = getEpubBook();
        var quranMeta = new XmlMapper().readValue(metaFile, Quran.class);
        var tocBuilder = new TocBuilder();
        var translationReader = new TranslationReader(quranFile, translationFile, quranMeta);
        var pageIterator = new PageIterator(quranMeta);
        Page prevPage = null;
        Map<Integer, Resource> resources = new TreeMap<>();
        for (int i = 1; i <= 604 && pageIterator.hasNext(); i++) {
            var currentPage = pageIterator.next();
            if (prevPage != null) {
                translationReader.read(prevPage, currentPage);
                var pageString = ebook.createPage(prevPage);
                tocBuilder.process(prevPage);
                Log.debugv("Page [{0}]", pageString);
                Log.infov("Page [{0}]", i);
                var resource = new Resource(pageString.getBytes(), "page_" + prevPage.getIndex() + ".html");
                resources.put(prevPage.getIndex(), resource);
                book.addResource(resource);
                book.getSpine().addSpineReference(new SpineReference(resource));
            }
            prevPage = currentPage;
        }

        var toc = tocBuilder.getToc();
        toc.getNavigations().forEach(navigation -> {
            var tocReference = new TOCReference("Juz " + navigation.getJuz().getIndex(), resources.get(navigation.getPage()));
            book.getTableOfContents().addTOCReference(
                    tocReference);
            navigation.getChildren().forEach(subnav -> {
                tocReference.addChildSection(
                        new TOCReference(subnav.getSura().getIndex() + ". " + subnav.getSura().getEname(), resources.get(subnav.getPage())));
            });
        });

        EpubWriter epubWriter = new EpubWriter();

        epubWriter.write(book, new FileOutputStream("quran_" + lang.toLowerCase() + ".epub"));
    }

    private Book getEpubBook() throws IOException {
        //create epub book
        Book book = new Book();
        var metadata = book.getMetadata();
        metadata.setTitles(List.of("Quran " + lang.toUpperCase()));
        book.setCoverImage(getResource( resources + "/data/cover.png", "cover.png"));
        book.getResources().add(getResource(resources + "/styles/style.css", "style.css"));
        return book;
    }

    private static InputStream getResource(String path) throws FileNotFoundException {
        return new FileInputStream(new File(path));
    }

    private static Resource getResource(String path, String href) throws IOException {
        return new Resource(getResource(path), href);
    }

    public static void main(String... args) {
        Quarkus.run(QuranEBookCommand.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
