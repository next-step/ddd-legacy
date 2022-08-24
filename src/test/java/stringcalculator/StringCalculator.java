package stringcalculator;

import java.util.stream.Stream;

public class StringCalculator {
    private static final String DEFAULT_SEPARATORS = "[,:]";
    private static final int ZERO = 0;

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return ZERO;
        }
        return Stream.of(text.split(DEFAULT_SEPARATORS))
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);
    }
}
