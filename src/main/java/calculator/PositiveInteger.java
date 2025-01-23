package calculator;

public class PositiveInteger {
    public static final PositiveInteger ZERO = PositiveInteger.of("0");
    private final String value;

    public PositiveInteger(String value, PositiveIntegerValidator positiveIntegerValidator) {
        positiveIntegerValidator.validation(value);
        this.value = value;
    }

    public static PositiveInteger of(String value) {
        return new PositiveInteger(value, new DefaultPositiveIntegerValidator());
    }

    public int toInt() {
        return Integer.parseInt(value);
    }
}
