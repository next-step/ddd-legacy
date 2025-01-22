package calculator;

public class PositiveInteger {
    private final String value;

    public PositiveInteger(String value, PositiveIntegerValidator stringValidator) {
        stringValidator.validation(value);
        this.value = value;
    }

    public static PositiveInteger of(String value) {
        return new PositiveInteger(value, new DefaultPositiveIntegerValidator());
    }

    public int toInt() {
        return Integer.parseInt(value);
    }
}
