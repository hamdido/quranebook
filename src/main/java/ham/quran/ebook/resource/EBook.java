package ham.quran.ebook.resource;

import ham.quran.ebook.BookConfiguration;
import ham.quran.ebook.model.Page;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EBook {
    @Inject
    BookConfiguration bookConfiguration;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance page(Page page);
        public static native TemplateInstance pagea(Page page);
    }

    public String createPage(Page page) {
        return "pagea".equals(bookConfiguration.template()) 
            ? Templates.pagea(page).render()
            : Templates.page(page).render();
    }
}
