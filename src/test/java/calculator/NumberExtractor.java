package calculator;

import java.util.List;
import java.util.stream.Stream;

public class NumberExtractor {

    private static final String DEFAULT_DELIMITER = "[,:]";

    public String extractDelimiter(final String text) {
        if (text.startsWith("//")) {
            return text.split("\n")[0].substring(2);
        }

        return DEFAULT_DELIMITER;
    }

    public String extractNumberText(final String text) {
        if (text.startsWith("//")) {
            return text.split("\n")[1];
        }

        return text;
    }

    public void validateNumber(final Integer number) {
        if (number < 0) {
            throw new RuntimeException("Negative numbers are not allowed");
        }
    }

    public List<Integer> extract(final String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        String delimiter = extractDelimiter(text);
        String numberText = extractNumberText(text);

        return Stream.of(numberText.split(delimiter))
            .map(Integer::parseInt)
            .peek(this::validateNumber)
            .toList();
    }
}
