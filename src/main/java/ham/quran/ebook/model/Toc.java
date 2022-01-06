package ham.quran.ebook.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Toc {
    private List<Navigation> navigations  = new ArrayList<>();
}
