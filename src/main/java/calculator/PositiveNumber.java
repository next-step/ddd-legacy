package calculator;

public class PositiveNumber {

    private static final int MINIMUM_VALUE = 0;

    public static final PositiveNumber ZERO = new PositiveNumber(MINIMUM_VALUE);

    private final int value;

    private PositiveNumber(int value) {
        if (value < MINIMUM_VALUE) {
            throw new RuntimeException();
        }
        this.value = value;
    }

    public static PositiveNumber from(int number) {
        if (number == MINIMUM_VALUE) {
            return ZERO;
        }
        return new PositiveNumber(number);
    }

    public static PositiveNumber from(String number) {
        return from(Integer.parseInt(number));
    }

    public int getValue() {
        return value;
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return add(positiveNumber.getValue());
    }

    public PositiveNumber add(int number) {
        return new PositiveNumber(this.value + number);
    }
}
