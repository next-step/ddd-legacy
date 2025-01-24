package calculator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    private static final Separators DEFAULT_SEPARATORS = Separators.of(new HashSet<>(List.of(",", ":")));
    private static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\n(.*)");

    private final Separators separators = DEFAULT_SEPARATORS;

    public Calculator() {
    }

    public int add(final String text) {
        if (Objects.isNull(text)) {
            return 0;
        }
        addCustomSeparator(text);
        final var elementText = extractElementText(text);
        if (Objects.isNull(elementText) || elementText.isBlank()) {
            return 0;
        }
        final var elements = separators.separate(elementText);
        return add(elements);
    }

    private int add(final List<Integer> elements) {
        return elements.stream()
                .map(Element::of)
                .mapToInt(Element::getElement)
                .sum();
    }

    private void addCustomSeparator(final String text) {
        Matcher m = CUSTOM_SEPARATOR_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            separators.addSeparator(customDelimiter);
        }
    }

    private String extractElementText(String text) {
        Matcher m = CUSTOM_SEPARATOR_PATTERN.matcher(text);
        if (m.find()) {
            return m.group(2);
        }
        return text;
    }
}
