package ham.quran.ebook.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.util.List;

@Data
public class Quran {
    private String type;
    private String version;
    private String copyright;
    private String license;
    @JacksonXmlElementWrapper(localName = "suras")
    private List<Sura> suras;
    @JacksonXmlElementWrapper(localName = "juzs")
    private List<Juz> juzs;
    @JacksonXmlElementWrapper(localName = "hizbs")
    private List<Quarter> hizbs;
    @JacksonXmlElementWrapper(localName = "manzils")
    private List<Manzil> manzils;
    @JacksonXmlElementWrapper(localName = "rukus")
    private List<Ruku> rukus;
    @JacksonXmlElementWrapper(localName = "pages")
    private List<Page> pages;
    @JacksonXmlElementWrapper(localName = "sajdas")
    private List<Sajda> sajdas;
}
