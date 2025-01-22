package calculator;

public class StringValue {
    private final String value;

    public StringValue(String value, StringValueValidator stringValidator) {
        stringValidator.validation(value);
        this.value = value;
    }
}
