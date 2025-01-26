package calculator;


public class Number {
    private final int value;
    private static final NumberValidator numberValidator = new NumberValidator();

    public Number(String input) {
        numberValidator.validate(input);
        this.value = Integer.parseInt(input);
    }

    public Number(int value) {
        numberValidator.validate(value);
        this.value = value;
    }

    public Number add(Number other) {
        return new Number(value + other.value);
    }

    public int value() {
        return value;
    }

    public static Number ZERO = new Number(0);

}