package ham.quran.ebook.resource;

import ham.quran.ebook.model.Navigation;
import ham.quran.ebook.model.Page;
import ham.quran.ebook.model.Toc;

public class TocBuilder {
    private Toc toc;
    private int index;
    public TocBuilder() {
        this.toc = new Toc();
    }

    public void process(Page page){
        page.getAyas().stream()
                .filter(aya -> aya.getJuzStart() != null)
                .findAny()
                .ifPresent(aya -> {
                    var nav = new Navigation(index++, page.getIndex());
                    nav.setJuz(aya.getJuzStart());
                    toc.getNavigations().add(nav);
                });
        page.getAyas().stream()
                .filter(aya -> aya.getSuraStart() != null)
                .findAny()
                .ifPresent(aya -> {
                    var nav = new Navigation(index++, page.getIndex());
                    nav.setSura(aya.getSuraStart());
                    toc.getNavigations().get(toc.getNavigations().size() - 1).getChildren().add(nav);
                });
    }

    public Toc getToc() {
        return toc;
    }
}
