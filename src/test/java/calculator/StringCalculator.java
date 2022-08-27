package calculator;

public class StringCalculator {

    public int add(final String input) {
        if (isBlank(input)) {
            return 0;
        }
        return sum(StringExpression.of(input));
    }

    private boolean isBlank(final String input) {
        return input == null || input.trim().length() == 0;
    }

    private int sum(final StringExpression expression) {
        return expression.parse()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
}
