package kitchenpos.stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        Matcher customDelimiterMatcher = Pattern.compile("//(.)\n(.*)").matcher(input);
        if (customDelimiterMatcher.find()) {
            String customDelimiter = customDelimiterMatcher.group(1);
            String numbers = customDelimiterMatcher.group(2);
            return sumNumbers(splitNumbers(numbers, customDelimiter));
        }

        return sumNumbers(splitNumbers(input, ",|:"));
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
