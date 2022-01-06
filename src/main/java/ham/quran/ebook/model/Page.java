package ham.quran.ebook.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Page extends Location{
    private Juz juz;
    private List<Aya> ayas = new ArrayList<>();
}
