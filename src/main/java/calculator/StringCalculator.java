package calculator;

public class StringCalculator {
    private final StringValue value;


    public StringCalculator(StringValue value) {
        this.value = value;
    }

    public static StringCalculator of(String value) {
        return new StringCalculator(new StringValue(value));
    }
}
