package calculator;

import common.numeric.NonNegativeNumber;

public class StringCalculator {

    public int sum(final String input) {
        if (isBlank(input)) {
            return 0;
        }
        return sum(StringExpression.of(input));
    }

    private int sum(final StringExpression expression) {
        return expression.parse()
            .stream()
            .reduce(new NonNegativeNumber(0), NonNegativeNumber::add)
            .getInt();
    }

    private boolean isBlank(final String input) {
        return input == null || input.trim().length() == 0;
    }
}
