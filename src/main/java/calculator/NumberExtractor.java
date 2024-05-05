package calculator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NumberExtractor {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String CUSTOM_DELIMITER_SUFFIX = "\n";

    private NumberExtractor() {
    }

    public static Numbers extract(final String text) {
        if (text == null || text.isEmpty()) {
            return new Numbers(List.of());
        }

        String delimiter = extractDelimiter(text);
        String numberText = extractNumberText(text);

        return new Numbers(Stream.of(numberText.split(delimiter))
            .map(Integer::parseInt)
            .peek(NumberExtractor::validateNumber)
            .toList());
    }

    private static String extractDelimiter(final String text) {
        if (text.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            int prefixLength = CUSTOM_DELIMITER_PREFIX.length();
            int suffixIndex = text.indexOf(CUSTOM_DELIMITER_SUFFIX);
            String customDelimiter = text.substring(prefixLength, suffixIndex);
            return DEFAULT_DELIMITER + "|" + Pattern.quote(customDelimiter);
        }

        return DEFAULT_DELIMITER;
    }

    private static String extractNumberText(final String text) {
        if (text.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            return text.split(CUSTOM_DELIMITER_SUFFIX)[1];
        }

        return text;
    }

    private static void validateNumber(final Integer number) {
        if (number < 0) {
            throw new RuntimeException("Negative numbers are not allowed");
        }
    }
}
