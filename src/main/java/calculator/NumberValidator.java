package calculator;

public class NumberValidator {
    private static final int MINIMUM_VALUE = 0;
    public void validate(String input) {
        if (!isBlank(input)) {
            validateNumber(input);
            validateNonNegative(parseNumber(input));
        }
    }
    public void validate(int input) {
        validateNonNegative(input);
    }

    private void validateNumber(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다: " + input);
        }
    }

    private void validateNonNegative(int num) {
        if (num < MINIMUM_VALUE ) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다: " + num);
        }
    }
    private int parseNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다: " + input);
        }
    }

    private boolean isBlank(String input) {
        return input == null || input.isBlank();
    }
}