package calculator.domain;

public class PositiveNumber {
    private static final String MINUS_NUMBER_EXCEPTION_MESSAGE = "음수는 입력할 수 없습니다.";

    private int value;

    private PositiveNumber(int value) {
        validationMinusNumber(value);
        this.value = value;
    }

    public static PositiveNumber from(int number) {
        return new PositiveNumber(number);
    }

    private void validationMinusNumber(int number) {
        if (number < 0) {
            throw new RuntimeException(MINUS_NUMBER_EXCEPTION_MESSAGE);
        }
    }

    public PositiveNumber add(PositiveNumber other) {
        int newValue = this.value + other.value;
        return new PositiveNumber(newValue);
    }

    public int getValue() {
        return value;
    }
}
