package calculator;

import java.util.Arrays;

public final class StringCalculator {

    private static final String DEFAULT_DELIMITER = "[,:]";

    public int add(final String expression) {
        if (isBlank(expression)) {
            return 0;
        }
        return Arrays.stream(expression.split(DEFAULT_DELIMITER))
                .mapToInt(Integer::valueOf)
                .sum();
    }

    private boolean isBlank(String expression) {
        return expression == null || expression.trim().length() == 0;
    }
}
