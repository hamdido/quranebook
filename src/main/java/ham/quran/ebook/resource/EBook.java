package ham.quran.ebook.resource;

import ham.quran.ebook.model.Page;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EBook {
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance page(Page page);
    }

    public String createPage(Page page){
        return Templates.page(page).render();
    }
}
