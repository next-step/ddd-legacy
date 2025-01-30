package calculator;

public record PositiveNumber(Integer value) {

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber {
        if (value < 0) {
            throw new NegativeNotAllowedException();
        }
    }

    public PositiveNumber add(PositiveNumber number) {
        return new PositiveNumber(value + number.value);
    }
}
