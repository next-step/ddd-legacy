package calculator;

public class StringCalculator {

    private final StringCalculatorParser parser = new StringCalculatorParser();

    public int add(final String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        final Integers integers = parser.parse(input);
        return integers.sum();
    }
}
