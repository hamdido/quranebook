package ham.quran.ebook.resource;

import ham.quran.ebook.model.Aya;
import ham.quran.ebook.model.Page;
import ham.quran.ebook.model.Quran;
import ham.quran.ebook.model.Toc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class TranslationReader {
    public static final String bismillah = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيم";
    private BufferedReader quran;
    private BufferedReader translation;
    private File quranFile;
    private File translationFile;
    private Quran quranMeta;
    private int ayaCount;
    public TranslationReader(File quranFile, File translationFile, Quran quranMeta)  {
       this.quranFile = quranFile;
       this.translationFile = translationFile;
       this.quranMeta = quranMeta;
    }

    public void read(Page page, Page nextPage) throws IOException {
        this.quran = new BufferedReader(new FileReader(quranFile));
        this.translation = new BufferedReader(new FileReader(translationFile));
        // add first verse
        addVerse(page);
        // iterate over the remaining verses until the end of the page (beginning of next page)
        addRemainingVerses(page, nextPage);

        // add translations
        addTranslations(page);
    }

    private void addTranslations(Page page) throws IOException {
        String tranlationText = null;
        for (var aya: page.getAyas()){
            if(tranlationText == null) {
                tranlationText  = findLine(translation, aya.getSura() , aya.getIndex());
            } else {
                tranlationText  = translation.readLine();
            }

            aya.setTranslation(tranlationText.split("\\|")[2]);
        }
    }

    /** Add remaining verses in the page by reading line by line */
    private void addRemainingVerses(Page page, Page nextPage) throws IOException {
        var currentLine = quran.readLine();
        while (currentLine != null) {
            var fragments  = currentLine.split("\\|");
            var sura = Integer.valueOf(fragments[0]);
            var aya = Integer.valueOf(fragments[1]);
            var nextLineText = fragments[2];
            // reach next page aya
            if(nextPage.getSura() == sura  && nextPage.getAya() == aya){
                break;
            }
            addAya(page, new Aya(aya, sura, nextLineText));
            currentLine = quran.readLine();
        }
    }

    private void addAya(Page page, Aya aya){
        aya.setCount(ayaCount);
        // mark staring points
        // sura start
        quranMeta.getSuras().stream()
                .filter( sura -> (sura.getStart()) == aya.getCount())
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

    private void addVerse(Page page) throws IOException {
        var firstAya  = findLine(quran, page.getSura() , page.getAya());
        var firstAyaText = firstAya.substring(firstAya.lastIndexOf("|") + 1, firstAya.length());
        addAya(page, new Aya(page.getAya(), page.getSura(), firstAyaText));
    }

    private String getAyaPattern(Integer sura, Integer aya){
        return sura + "\\|" + aya + "\\|.+";
    }

    private String findLine(BufferedReader reader, Integer sura, Integer aya) throws IOException {
        var line = reader.readLine();
        while(line != null){
            if(line.startsWith(sura + "|" + aya + "|")) {
                return line;
            }
            line = reader.readLine();
        }
        return null;
    }
}
