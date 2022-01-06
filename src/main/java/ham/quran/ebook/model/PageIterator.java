package ham.quran.ebook.model;

import java.util.Iterator;
import java.util.Optional;

public class PageIterator implements Iterator<Page>{
    private Quran meta;
    private Iterator<Page> pageIterator;
    public PageIterator(Quran meta){
        this.meta = meta;
        pageIterator = this.meta.getPages().iterator();
    }

    @Override
    public boolean hasNext() {
        return pageIterator.hasNext();
    }

    @Override
    public Page next() {
        var nextPage = pageIterator.next();
        Juz currentJuz = meta.getJuzs().get(0);
        for (var juz: meta.getJuzs()){
            if(juz.getSura() > nextPage.getSura() || (juz.getSura() == nextPage.getSura() && juz.getAya() > nextPage.getAya())){
                break;
            }
            currentJuz = juz;
        }
        nextPage.setJuz(currentJuz);
        return nextPage;
    }
}
