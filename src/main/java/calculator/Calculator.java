package calculator;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Calculator {
    public static final Separators DEFAULT_SEPARATORS = Separators.of(Set.of(",", ":"));

    private final Separators separators = DEFAULT_SEPARATORS;

    public Calculator() {
    }

    public int add(final String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return 0;
        }
        final var elements = separators.separate(text);
        return add(elements);
    }

    private int add(final List<Element> elements) {
        return elements.stream()
                .mapToInt(Element::getElement)
                .sum();
    }
}
