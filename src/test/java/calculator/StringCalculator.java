package calculator;

import java.util.Arrays;

public class StringCalculator {

    private static final int ZERO = 0;

    public int add(final String text) {
        Text input = new Text(text);

        if (input.isNullOrEmpty()) {
            return ZERO;
        }
        if (input.isContainComma()) {
            String[] tokens = input.spitComma();
            return Arrays.stream(tokens)
                    .mapToInt(Integer::parseInt)
                    .sum();
        }
        return Integer.parseInt(text);
    }
}
