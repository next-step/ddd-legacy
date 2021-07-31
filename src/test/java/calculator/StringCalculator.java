package calculator;

import java.util.Objects;

public class StringCalculator {
    private static final int DEFAULT_RETURN_VALUE = 0;
    private static final StringTokenizer stringTokenizer = new StringTokenizer();

    public int add(final String text) {
        if (Objects.isNull(text)) {
            return DEFAULT_RETURN_VALUE;
        }

        final StringOperands stringOperands = stringTokenizer.tokenize(text);
        if (stringOperands.isEmpty()) {
            return DEFAULT_RETURN_VALUE;
        }

        return calculate(stringOperands);
    }

    private int calculate(final StringOperands stringOperands) {
        return stringOperands.stream()
                .mapToInt(StringOperand::parseInt)
                .sum();
    }
}
