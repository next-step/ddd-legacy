package stringcalculator;

public class PositiveNumber {

    private static final int MINIMUM_VALUE = 0;
    private int value;

    public PositiveNumber(final String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(final int number) {
        if (number < MINIMUM_VALUE) {
            throw new RuntimeException("0보다 큰 값만 가능합니다.");
        }
        this.value = number;
    }

    public int toInt() {
        return value;
    }
}
