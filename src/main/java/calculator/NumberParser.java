package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberParser {
    private static final String REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final String DEFAULT_REGEX = "[,:]";

    public static PositiveNumbers parse(String str) {
        List<String> numbers = extractNumbers(str);
        return PositiveNumbers.fromString(numbers);
    }

    private static List<String> extractNumbers(String str) {
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] numbers = m.group(2).split(customDelimiter);
            return List.of(numbers);
        }

        return List.of(str.split(DEFAULT_REGEX));
    }
}
