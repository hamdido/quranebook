package ham.quran.ebook;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "book")
public interface BookConfiguration {
    String version();
    String template();
    String style();
}
