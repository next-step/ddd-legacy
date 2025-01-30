package calculator;

public class StringCalculator {
    private static final Integer DEFAULT_RESULT = 0;

    public int add(String numbersString) {
        if (!InputValidator.isValid(numbersString)) {
            return DEFAULT_RESULT;
        }

        String[] numbersArray = DelimiterParser.parse(numbersString);

        return NumbersParser.parse(numbersArray);
    }
}
