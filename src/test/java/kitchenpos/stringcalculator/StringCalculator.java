package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (customDelimiterMatcher.find()) {
            String customDelimiter = customDelimiterMatcher.group(DELIMITER_GROUP);
            String numbers = customDelimiterMatcher.group(NUMBERS_GROUP);
            return sumNumbers(splitNumbers(numbers, customDelimiter));
        }

        return sumNumbers(splitNumbers(input, DEFAULT_DELIMITERS));
    }

    private String[] splitNumbers(String input, String delimiterPattern) {
        return input.split(delimiterPattern);
    }

    private int sumNumbers(String[] numberStrings) {
        return Arrays.stream(numberStrings)
                     .mapToInt(Integer::parseInt)
                     .peek(this::validateNegativeNumber)
                     .sum();
    }

    private void validateNegativeNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수는 포함될 수 없습니다: " + number);
        }
    }
}
