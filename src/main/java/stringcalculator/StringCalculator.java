package stringcalculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int SINGLE_INPUT_LENGTH = 1;
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+-]?\\d*(\\.\\d+)?");

    private StringCalculator() {
    }

    public static int add(String text) {
        if (isNullOrEmpty(text)) {
            return PositiveNumber.MIN_NUMBER;
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
        return Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

}
