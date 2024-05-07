package calculator;

import java.util.Collections;
import java.util.stream.Collectors;

public class NumberExtractor {

    private static final int AFTER_CUSTOM_DELIMITER = 1;

    private NumberExtractor() {
    }

    public static Numbers extract(final String text) {
        if (text == null || text.isEmpty()) {
            return new Numbers(Collections.emptyList());
        }
        Delimiter delimiter = Delimiter.of(text);
        String numberText = extractNumberText(text);

        return delimiter.split(numberText)
            .map(Integer::parseInt)
            .map(PositiveNumber::new)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Numbers::new));
    }

    private static String extractNumberText(final String text) {
        if (text.startsWith(Delimiter.CUSTOM_DELIMITER_PREFIX)) {
            return text.split(Delimiter.CUSTOM_DELIMITER_SUFFIX)[AFTER_CUSTOM_DELIMITER];
        }

        return text;
    }
}
