package calculator;

public class PositiveNumber {

    private final int value;

    public PositiveNumber(final int value) {
        validPositiveNumber(value);
        this.value = value;
    }

    private void validPositiveNumber(final int value) {
        if (value < 0)
            throw new RuntimeException("양수만 사용 가능합니다.");
    }

    public static PositiveNumber toNumberValue(final String value) {
        return new PositiveNumber(Integer.parseInt(value));
    }

    public PositiveNumber addValue(final PositiveNumber number) {
        return new PositiveNumber(value + number.value);
    }

    public int getValue() {
        return value;
    }

}
