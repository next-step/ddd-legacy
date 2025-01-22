package calculator;

public class StringValue {
    private final String value;

    public StringValue(String value, StringValueValidator stringValidator) {
        stringValidator.validation(value);
        this.value = value;
    }

    public static StringValue of(String value) {
        return new StringValue(value, new DefaultStringValueValidator());
    }

    public int toInt() {
        return Integer.parseInt(value);
    }
}
