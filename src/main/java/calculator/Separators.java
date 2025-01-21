package calculator;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Separators {
    private Set<String> separators;

    private Separators(Set<String> separators) {
        this.separators = separators;
    }

    public static Separators of(Set<String> separators) {
        return new Separators(separators);
    }

    public List<Element> separate(String text) {
        String regex = String.join("|", separators);
        return Stream.of(text.split(regex))
                .map(Element::of)
                .toList();
    }
}
