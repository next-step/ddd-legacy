package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int MIN_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;
    private static final String TOKEN_DELIMITER = ",|:";

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int CUSTOM_DELIMITER_INPUT_INDEX = 2;

    private StringCalculator() {
    }

    public static int add(String text) {
        if (text == null  || text.isEmpty()) {
            return MIN_NUMBER;
        }
        if (text.length() == SINGLE_INPUT_LENGTH && isInteger(text)) {
            return Integer.parseInt(text);
        }
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            return sumByCustomDelimiter(matcher);
        }
        return sum(text.split(TOKEN_DELIMITER));
    }

    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int sumByCustomDelimiter(Matcher matcher) {
        String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
        String[] tokens = matcher.group(CUSTOM_DELIMITER_INPUT_INDEX).split(customDelimiter);
        return sum(tokens);
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
