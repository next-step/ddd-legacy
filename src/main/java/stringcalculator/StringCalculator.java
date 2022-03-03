package stringcalculator;

import java.util.regex.Pattern;

public class StringCalculator {

    private static final int MIN_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+-]?\\d*(\\.\\d+)?");

    private StringCalculator() {
    }

    public static int add(String text) {
        if (isNullOrEmpty(text)) {
            return MIN_NUMBER;
        }
        if (isSingleInputInteger(text)) {
            return Integer.parseInt(text);
        }
        return sum(StringTokenDelimiter.split(text));
    }

    private static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private static boolean isSingleInputInteger(String text) {
        if (text.length() != SINGLE_INPUT_LENGTH) {
            return false;
        }
        return isInteger(text);
    }

    private static boolean isInteger(String text) {
        return NUMERIC_PATTERN.matcher(text).matches();
    }

    private static int sum(String[] tokens) {
        int sum = MIN_NUMBER;
        for (String token : tokens) {
            int number = Integer.parseInt(token);
            validateNegative(number);
            sum+=number;
        }
        return sum;
    }

    private static void validateNegative(int number) {
        if (number < MIN_NUMBER) {
            throw new RuntimeException();
        }
    }
}
