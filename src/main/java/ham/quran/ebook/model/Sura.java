package ham.quran.ebook.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Sura {
    @JacksonXmlProperty(isAttribute = true)
    private int index;
    @JacksonXmlProperty(isAttribute = true)
    private int ayas;
    @JacksonXmlProperty(isAttribute = true)
    private int start;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String tname;
    @JacksonXmlProperty(isAttribute = true)
    private String ename;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
    @JacksonXmlProperty(isAttribute = true)
    private int order;
    @JacksonXmlProperty(isAttribute = true)
    private int rukus;
}
