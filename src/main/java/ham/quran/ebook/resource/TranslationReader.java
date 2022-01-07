package ham.quran.ebook.resource;

import ham.quran.ebook.model.Aya;
import ham.quran.ebook.model.Page;
import ham.quran.ebook.model.Quran;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TranslationReader {
    public static final String bismillah = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيم";
    private Quran quranMeta;
    private int ayaCount;
    private List<Aya> suras;
    private List<Aya> translations;

    public TranslationReader(File quranFile, File translationFile, Quran quranMeta) throws IOException {
        this.quranMeta = quranMeta;
        this.suras = readLines(new BufferedReader(new FileReader(quranFile)));
        this.translations = readLines(new BufferedReader(new FileReader(translationFile)));
    }

    public void read(Page page, Page nextPage) throws IOException {
        addVerses(page, nextPage);
        addTranslations(page);
    }

    /**
     * Add verses with original text
     */
    private void addVerses(Page page, Page nextPage) throws IOException {
        var line = findLine(suras, page.getSura(), page.getAya());
        var position = suras.indexOf(line.get());
        while (position < suras.size()) {
            var aya = suras.get(position++);
            if (nextPage.getSura() == aya.getSura() && nextPage.getAya() == aya.getIndex()) {
                break;
            }
            addAya(page, aya);
        }
    }

    private void addTranslations(Page page) throws IOException {
        for (var aya : page.getAyas()) {
            findLine(translations, aya.getSura(), aya.getIndex()).ifPresent(trans -> aya.setTranslation(trans.getText()));
        }
    }

    private void addAya(Page page, Aya aya) {
        aya.setCount(ayaCount);
        // sura start
        quranMeta.getSuras().stream()
                .filter(sura -> (sura.getStart()) == aya.getCount())
                .findFirst()
                .ifPresent(sura -> {
                    aya.setSuraStart(sura);
                    if (aya.getText().indexOf(bismillah) != -1 && aya.getText().length() > bismillah.length()) {
                        aya.setBeginning(aya.getText().substring(bismillah.length() + 1, aya.getText().length()));
                    }
                });
        // juz start
        quranMeta.getJuzs().stream()
                .filter(juz -> juz.getSura() == aya.getSura() && juz.getAya() == aya.getIndex())
                .findFirst()
                .ifPresent(juz -> aya.setJuzStart(juz));
        page.getAyas().add(aya);
        ayaCount++;
    }

    private Optional<Aya> findLine(List<Aya> suras, Integer sura, Integer aya) throws IOException {
        return suras.stream().filter(entry -> entry.getIndex() == aya && entry.getSura() == sura).findFirst();
    }

    private ArrayList<Aya> readLines(BufferedReader reader) throws IOException {
        var allSuras = new ArrayList<Aya>();
        var line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            var fragments = line.split("\\|");
            var sura = Integer.valueOf(fragments[0]);
            var aya = Integer.valueOf(fragments[1]);
            var nextLineText = fragments[2];
            allSuras.add(new Aya(aya, sura, nextLineText));
            line = reader.readLine();
        }
        return allSuras;
    }
}
