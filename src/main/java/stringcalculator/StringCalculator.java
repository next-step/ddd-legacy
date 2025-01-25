package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return getSum(tokens);
        }

        try {
            var number = Integer.parseInt(text);
            checkForNegative(number);
            return number;
        } catch (NumberFormatException e) {
            String[] values = text.split("[,:]");
            return getSum(values);
        }
    }

    private int getSum(String[] tokens) {
        return Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .peek(this::checkForNegative)
                .sum();
    }

    private void checkForNegative(int number) {
        if (number < 0) {
            throw new RuntimeException("Negative numbers not allowed: " + number);
        }
    }
}
