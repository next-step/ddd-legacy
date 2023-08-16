package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final String DEFAULT_REGEX = "[,:]";

    public int run(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        if (str.length() == 1) {
            return Integer.parseInt(str);
        }

        String[] numbers = extractNumbers(str);
        return Arrays.stream(numbers)
                .map(Number::fromString)
                .reduce(new Number(0), Number::plus)
                .getValue();
    }

    private String[] extractNumbers(String str) {
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return str.split(DEFAULT_REGEX);
    }
}
