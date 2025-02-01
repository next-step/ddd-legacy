package calculate.domain;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringNumberParser {

    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER = "//(.)\\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(CUSTOM_DELIMITER);

    private StringNumberParser() {
    }

    public static List<Number> extractNumbersFrom(String input) {
        if (input == null || input.isEmpty()) {
            return List.of(new Number(0));
        }

        if (input.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            return splitByCustomDelimiter(input);
        }

        return Arrays.stream(input.split(DEFAULT_DELIMITER))
                .map(value -> new Number(convertToNumber(value)))
                .toList();
    }

    private static List<Number> splitByCustomDelimiter(String input) {
        final Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String delimiter = matcher.group(1);
            String formula = matcher.group(2);

            return Arrays.stream(formula.split(delimiter))
                    .map(value -> new Number(convertToNumber(value)))
                    .toList();
        }
        return Collections.emptyList();
    }

    private static int convertToNumber(final String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

}
