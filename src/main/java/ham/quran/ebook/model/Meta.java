package ham.quran.ebook.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Meta {
    @NonNull
    private List<Page> pages;
}
