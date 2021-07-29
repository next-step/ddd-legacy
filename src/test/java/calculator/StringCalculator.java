package calculator;

import java.util.Arrays;

public class StringCalculator {

    private static final int ZERO = 0;

    public int add(final String text) {
        if (isNullOrEmpty(text)) {
            return ZERO;
        }
        if (text.contains(",")) {
            String[] tokens = text.split(",");
            return Arrays.stream(tokens)
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
        return Integer.parseInt(text);
    }

    private boolean isNullOrEmpty(final String text) {
        return text == null || text.isEmpty();
    }
}
