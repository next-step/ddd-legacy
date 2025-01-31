package calculate.domain;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberGroupsParser {

    private NumberGroupsParser() {
    }

    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER = "//(.)\\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(CUSTOM_DELIMITER);

    public static List<Number> parse(String input) {
        if (input == null || input.isEmpty()) {
            return List.of(new Number(0));
        }

        if (input.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            return customDelimiterSplit(input);
        }

        return Arrays.stream(input.split(DEFAULT_DELIMITER))
                .map(Number::new)
                .toList();
    }

    private static List<Number> customDelimiterSplit(String input) {
        final Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String delimiter = matcher.group(1);
            String formula = matcher.group(2);

            return Arrays.stream(formula.split(delimiter))
                    .map(Number::new)
                    .toList();
        }
        return Collections.emptyList();
    }

}
