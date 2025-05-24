package ham.quran.ebook.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Navigation {
    private final int index;
    private final int page;
    private Sura sura;
    private Juz juz;
    private List<Navigation> children = new ArrayList<>();
}
