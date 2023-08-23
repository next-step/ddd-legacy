package calculator;

public class StringCalculator {
    private static final StringCalculatorParser parser = new StringCalculatorParser();

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        return parser.toPositiveNumbers(text)
                .sum()
                .getNumber();
    }
}
