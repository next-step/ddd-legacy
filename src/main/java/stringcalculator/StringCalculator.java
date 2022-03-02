package stringcalculator;

public class StringCalculator {

    private static final int MIN_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;

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
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
