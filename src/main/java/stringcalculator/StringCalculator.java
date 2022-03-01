package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_PATTERNS = "[,]|[:]";

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        return Arrays.stream(text.split(DEFAULT_DELIMITER_PATTERNS))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}