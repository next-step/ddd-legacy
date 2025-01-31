package calculator;

public class NumberValidator {
    private static final int MINIMUM_VALUE = Number.ZERO.value();

    private NumberValidator() {}

    public static NumberValidator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final NumberValidator INSTANCE = new NumberValidator();
    }

    public void validate(String input) {
        if (!isBlank(input)) {
            int parsedNumber = validateAndParseNumber(input);
            validateNonNegative(parsedNumber);
        }
    }

    private int validateAndParseNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다: " + input);
        }
    }

    private void validateNonNegative(int num) {
        if (num < MINIMUM_VALUE ) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다: " + num);
        }
    }

    private boolean isBlank(String input) {
        return input == null || input.isBlank();
    }
}
