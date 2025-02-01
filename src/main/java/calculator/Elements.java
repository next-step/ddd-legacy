package calculator;

import java.util.List;

public class Elements {
    private final List<Element> elements;

    public Elements(final List<Integer> elements) {
        this.elements = elements
            .stream()
            .map(Element::of)
            .toList();
    }

    public int sum() {
        return elements.stream()
            .mapToInt(Element::getElement)
            .sum();
    }
}
