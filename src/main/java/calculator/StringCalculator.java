package calculator;

public class StringCalculator {
    private static final PositiveInteger ZERO = PositiveInteger.of("0");
    private final PositiveIntegers positiveIntegers;

    public StringCalculator(PositiveIntegers positiveIntegers) {
        this.positiveIntegers = positiveIntegers;
    }

    public static StringCalculator of(String str) {
        if (str == null || str.isEmpty()) {
            return new StringCalculator(new PositiveIntegers(ZERO));
        }
        InputValue inputValue = InputValue.of(str);
        return new StringCalculator(new PositiveIntegers(inputValue.getPositiveIntegers()));
    }

    public int sum() {
        return this.positiveIntegers.intValues()
                .stream()
                .reduce(0, Integer::sum);
    }
}
