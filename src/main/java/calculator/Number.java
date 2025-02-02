package calculator;


public class Number {
    public static final Number ZERO = new Number(0);
    private static final NumberValidator numberValidator = NumberValidator.getInstance();

    private final int value;

    private Number(int value) {  // private 생성자
        this.value = value;
    }

    public static Number from(String input) {
        numberValidator.validate(input);
        return new Number(Integer.parseInt(input));
    }

    public Number add(Number other) {
        return new Number(value + other.value);
    }

    public int value() {
        return value;
    }



}