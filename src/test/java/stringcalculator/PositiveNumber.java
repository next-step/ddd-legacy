package stringcalculator;

public class PositiveNumber extends Number {

    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public PositiveNumber sum(PositiveNumber other) {
        return new PositiveNumber(value + other.value);
    }
}
