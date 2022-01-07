package ham.quran.ebook;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import org.eclipse.microprofile.config.inject.ConfigProperties;

@ConfigMapping(prefix = "book")
public interface BookConfiguration {
    String version();
}
