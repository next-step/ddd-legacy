package calculator;

public class StringCalculator {
    private static final Integer DEFAULT_RESULT = 0;

    private final InputValidator inputValidator;
    private final DelimiterParser delimiterParser;

    public StringCalculator(InputValidator inputValidator, DelimiterParser delimiterParser) {
        this.inputValidator = inputValidator;
        this.delimiterParser = delimiterParser;
    }

    public int add(String numbersString) {
        if (!inputValidator.isValid(numbersString)) {
            return DEFAULT_RESULT;
        }

        Numbers numbers = delimiterParser.parse(numbersString);
        return numbers.sum();
    }
}
