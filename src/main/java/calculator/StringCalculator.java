package calculator;

public class StringCalculator {
    private final PositiveIntegers values;

    public StringCalculator(PositiveIntegers values) {
        this.values = values;
    }

    public static StringCalculator of(String str) {
        return new StringCalculator(PositiveIntegers.of(str));
    }

    public int sum() {
        return this.values.intValues()
                .stream()
                .reduce(0, Integer::sum);
    }
}
