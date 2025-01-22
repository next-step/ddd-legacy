package calculator;

public class StringCalculator {
    private final StringValues values;

    public StringCalculator(StringValues values) {
        this.values = values;
    }

    public static StringCalculator of(String str) {
        return new StringCalculator(new StringValues(str, new DefaultStringValuesValidator()));
    }

    public int sum() {
        return this.values.intValues()
                .stream()
                .reduce(0, Integer::sum);
    }
}
