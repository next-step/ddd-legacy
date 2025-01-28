package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringAddCalculator {

    private StringAddCalculator() {
        throw new IllegalStateException("Utility class");
    }

    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DELIMITER = "[,:]";
    private static final int DEFAULT_RESULT = 0;

    public static int parseAndSum(String value) {

        if (isNullOrEmpty(value)) {
            return DEFAULT_RESULT;
        }

        return Arrays.stream(split(value))
                     .map(PositiveNumber::new)
                     .reduce(PositiveNumber::plus)
                     .map(PositiveNumber::getNumber)
                     .orElse(0);
    }

    private static String[] split(String value) {
        String[] splitValue = value.split(DELIMITER);
        Matcher matcher = CUSTOM_PATTERN.matcher(value);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            splitValue = matcher.group(2).split(customDelimiter);
        }

        return splitValue;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
