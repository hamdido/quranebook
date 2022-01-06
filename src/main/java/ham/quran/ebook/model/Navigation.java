package ham.quran.ebook.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Navigation {
    @NonNull
    private int index;
    @NonNull
    private int page;
    private Sura sura;
    private Juz juz;
    private List<Navigation> children = new ArrayList<>();
}
