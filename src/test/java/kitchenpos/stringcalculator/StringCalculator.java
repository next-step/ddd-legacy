package kitchenpos.stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final int DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        Matcher customDelimiterMatcher = Pattern.compile(CUSTOM_DELIMITER).matcher(input);
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
        int total = 0;

        for (String numberString : numberStrings) {
            int number = Integer.parseInt(numberString);

            if (number < 0) {
                throw new RuntimeException();
            }

            total += number;
        }

        return total;
    }
}
