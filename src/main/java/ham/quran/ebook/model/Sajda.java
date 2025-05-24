package ham.quran.ebook.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Sajda extends Location{
    private String type;
}
