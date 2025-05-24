package ham.quran.ebook.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Aya{
    private final int index;
    private final int sura;
    @NonNull
    private final String text;
    private String beginning;
    private String translation;
    private int count;
    private Sura suraStart;
    private Juz juzStart;
}
