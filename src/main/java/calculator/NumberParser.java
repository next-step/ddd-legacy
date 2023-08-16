package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NumberParser {
    private static final String REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final String DEFAULT_REGEX = "[,:]";

    public static List<Number> parse(String str) {
        String[] numbers = extractNumbers(str);
        return Arrays.stream(numbers)
                .map(Number::fromString)
                .collect(Collectors.toList());
    }

    private static String[] extractNumbers(String str) {
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2)
                    .split(customDelimiter);
        }

        return str.split(DEFAULT_REGEX);
    }
}
