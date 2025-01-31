package calculator;

public class Number {
    private static final String NEGATIVE_NUMBERS_MESSAGE = "음수는 허용되지 않습니다.";
    private static final String NON_NUMERIC_MESSAGE = "숫자가 아닌 값이 포함되어 있습니다.";

    private final int value;

    private Number(int value) {
        validateNonNegative(value);
        this.value = value;
    }

    public static Number from(String input) {
        validateNumeric(input);
        try {
            return new Number(Integer.parseInt(input.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(NON_NUMERIC_MESSAGE);
        }
    }

    private static void validateNumeric(String input) {
        if (!input.trim().matches("^-?\\d+$")) {
            throw new IllegalArgumentException(NON_NUMERIC_MESSAGE);
        }
    }

    private void validateNonNegative(int num) {
        if (num < 0) {
            throw new RuntimeException(NEGATIVE_NUMBERS_MESSAGE);
        }
    }

    public int getValue() {
        return value;
    }
}
