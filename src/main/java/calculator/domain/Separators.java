package calculator.domain;

import java.util.List;

public class Separators {

    private static final DefaultSeparator defaultSeparator = new DefaultSeparator();
    private final List<Separator> values;

    public Separators(List<Separator> values) {
        this.values = values;
    }

    public static Separators generate() {
        List<Separator> values = List.of(
            new CustomSeparator()
        );

        return new Separators(values);
    }

    public List<String> splitText(String text) {
        Separator separator = getMatchSeparator(text);
        return separator.split(text);
    }

    private Separator getMatchSeparator(String text) {
        return values.stream()
            .filter(separator -> separator.isMatchWithText(text))
            .findAny()
            .orElse(defaultSeparator);
    }
}
