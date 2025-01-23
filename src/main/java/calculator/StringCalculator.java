package calculator;

public class StringCalculator {
    private final PositiveIntegerCalculator positiveIntegerCalculator;

    public StringCalculator(PositiveIntegerCalculator positiveIntegerCalculator) {
        this.positiveIntegerCalculator = positiveIntegerCalculator;
    }

    public static StringCalculator of(String str) {
        if (str == null || str.isEmpty()) {
            return new StringCalculator(new PositiveIntegerCalculator(PositiveInteger.ZERO));
        }
        InputValue inputValue = InputValue.of(str);
        return new StringCalculator(new PositiveIntegerCalculator(inputValue.getPositiveIntegers()));
    }

    public int sum() {
        return this.positiveIntegerCalculator.sum();
    }
}
