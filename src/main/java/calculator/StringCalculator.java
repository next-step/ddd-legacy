package calculator;

public class StringCalculator {
    private static final String ZERO = "0";
    private final PositiveIntegers values;

    public StringCalculator(PositiveIntegers values) {
        this.values = values;
    }

    public static StringCalculator of(String str) {
        if (str == null || str.isEmpty()) {
            return new StringCalculator(new PositiveIntegers(PositiveInteger.of(ZERO)));
        }
        InputValue inputValue = InputValue.of(str);
        return new StringCalculator(new PositiveIntegers(inputValue.getPositiveIntegers()));
    }

    public int sum() {
        return this.values.intValues()
                .stream()
                .reduce(0, Integer::sum);
    }
}
