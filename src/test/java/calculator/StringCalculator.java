package calculator;

import java.util.Objects;
import java.util.function.IntBinaryOperator;

public class StringCalculator {
    private static final int DEFAULT_RETURN_VALUE = 0;

    private final StringTokenizer stringTokenizer = new StringTokenizer();

    private int calculate(final String text, final IntBinaryOperator intBinaryOperator) {
        if (Objects.isNull(text)) {
            return DEFAULT_RETURN_VALUE;
        }

        final StringOperands stringOperands = stringTokenizer.tokenize(text);
        if (stringOperands.isEmpty()) {
            return DEFAULT_RETURN_VALUE;
        }

        return stringOperands.reduce(DEFAULT_RETURN_VALUE, intBinaryOperator);
    }

    public int add(final String text) {
        return calculate(text, Integer::sum);
    }
}
