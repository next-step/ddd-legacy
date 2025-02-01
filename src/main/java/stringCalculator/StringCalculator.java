package stringCalculator;

public class StringCalculator {
    private static final int DEFAULT_VALUE_IN_INVALID_CASE = 0;
    private static final ParsingStrategy PARSING_STRATEGY;
    static {
        PARSING_STRATEGY = new StringParsingStrategy();
    }

    public int add(final String text) {
        final InputExpression inputExpression = InputExpression.parse(PARSING_STRATEGY, text);
        final Numbers numbers = inputExpression.numbers();
        if (numbers.isEmpty()) {
            return DEFAULT_VALUE_IN_INVALID_CASE;
        }
        if (numbers.isNegative()) {
            throw new IllegalArgumentException();
        }
        return numbers.sum();
    }
}


