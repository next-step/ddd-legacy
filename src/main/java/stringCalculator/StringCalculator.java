package stringCalculator;

public class StringCalculator {
    private static final int DEFAULT_VALUE_IN_INVALID_CASE = 0;

    public int add(final String text) {
        final InputExpression inputExpression = InputExpression.parse(text);
        final Numbers numbers = inputExpression.numbers();
        if (numbers.emptyOrNull()) {
            return DEFAULT_VALUE_IN_INVALID_CASE;
        }
        if (numbers.isContainMinus()) {
            throw new IllegalArgumentException();
        }
        return numbers.sum();
    }
}


