package calculator;

import java.util.Arrays;

public class StringCalculator {

    private static final int EMPTY_EXPRESSION_RESULT = 0;

    public int add(String text) {
        if (text == null) {
            return EMPTY_EXPRESSION_RESULT;
        }

        final var expression = new AdditionExpression(text);

        final String[] tokens = expression.splitTokensByDelimiter();

        return Arrays.stream(tokens)
                .map(PositiveInteger::parse)
                .reduce(PositiveInteger.ZERO, PositiveInteger::plus)
                .toInt();
    }
}
