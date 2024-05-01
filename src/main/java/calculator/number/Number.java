package calculator.number;

public class Number {

    private final int value;

    public static final Number ZERO = new Number(0);

    public Number(int value) {
        this.value = value;
    }

    public Number(String value) {
        this(Integer.parseInt(value));
    }

    public boolean isNegative() {
        return this.value < 0;
    }

    public int getValue() {
        return this.value;
    }

}
