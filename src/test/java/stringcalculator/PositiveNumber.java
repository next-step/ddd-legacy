package stringcalculator;

public class PositiveNumber {

    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수는 될 수 없습니다 value=%d".formatted(value));
        }
        this.value = value;
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(value + other.value);
    }

    public int value() {
        return value;
    }
}
