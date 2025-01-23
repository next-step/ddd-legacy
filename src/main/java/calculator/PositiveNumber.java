package calculator;

public class PositiveNumber {
    private static final String NEGATIVE_NUMBER_EXCEPTION = "음수를 넣을 수 없습니다.";

    private final Number number;

    public PositiveNumber(Number number) {
        validatePositive(number);
        this.number = number;
    }

    private void validatePositive(Number number) {
        if (number.isNegative()) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_EXCEPTION);
        }
    }

    public int getPrimitiveValue() {
        return number.getValue();
    }
}
