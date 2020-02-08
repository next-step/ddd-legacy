package calculator;

public class Number {
    private Integer value;

    private Number(int value) {
        validNotNegativeNumber(value);
        this.value = value;
    }

    public static Number from(int value) {
        return new Number(value);
    }

    public Integer getValue() {
        return value;
    }

    public Number sum(Number number) {
        return new Number(this.value + number.getValue());
    }

    private void validNotNegativeNumber(Integer value) {
        if (value < 0) {
            throw new RuntimeException("The number should not be negative");
        }
    }
}
