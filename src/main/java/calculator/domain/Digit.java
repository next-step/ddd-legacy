package calculator.domain;

public class Digit {
    private static final String MINUS_NUMBER_EXCEPTION_MESSAGE = "음수는 입력할 수 없습니다.";

    private int value;

    private Digit(int value) {
        validationMinusNumber(value);
        this.value = value;
    }

    public static Digit from(int number) {
        return new Digit(number);
    }

    private void validationMinusNumber(int number) {
        if (number < 0) {
            throw new RuntimeException(MINUS_NUMBER_EXCEPTION_MESSAGE);
        }
    }

    public Digit add(Digit other) {
        int newValue = this.value + other.value;
        return new Digit(newValue);
    }

    public int getValue() {
        return value;
    }
}
