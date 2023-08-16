package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberParser {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);
    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";

    public static Numbers parse(String str) {
        List<String> numbers = extractNumbers(str);
        return Numbers.fromString(numbers);
    }

    private static List<String> extractNumbers(String str) {
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] numbers = m.group(2).split(customDelimiter);
            return List.of(numbers);
        }

        return List.of(str.split(DEFAULT_DELIMITER_REGEX));
    }
}
