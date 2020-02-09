package calculator;

public class PositiveNumber {

    private int value;

    public static final PositiveNumber ZERO = new PositiveNumber(0);

    public static PositiveNumber valueOf(String text) {
        return new PositiveNumber(Integer.parseInt(text));
    }

    private PositiveNumber(int number) {
        isPositiveNumber(number);
        this.value = number;
    }

    public int getValue() {
        return value;
    }

    public boolean isPositiveNumber(int number) {
        if (number < 0) {
            throw new NegativeNumberException();
        }

        return true;
    }

    public PositiveNumber sum(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.getValue());
    }
}
