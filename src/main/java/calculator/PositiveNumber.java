package calculator;

public class PositiveNumber {
    private Integer value;

    private PositiveNumber(int value) {
        validNotNegativeNumber(value);
        this.value = value;
    }

    public static PositiveNumber from(int value) {
        return new PositiveNumber(value);
    }

    Integer getValue() {
        return value;
    }

    PositiveNumber sum(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.getValue());
    }

    private void validNotNegativeNumber(Integer value) {
        if (isPositive(value)) {
            throw new RuntimeException("The number should not be negative");
        }
    }

    private boolean isPositive(Integer value) {
        return value < 0;
    }
}
