package ham.quran.ebook.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Aya{
    @NonNull
    private int index;
    @NonNull
    private int sura;
    @NonNull
    private String text;
    private String beginning;
    private String translation;
    private int count;
    private Sura suraStart;
    private Juz juzStart;
}
