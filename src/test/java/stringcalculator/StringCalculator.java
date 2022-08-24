package stringcalculator;

import java.util.Arrays;

public class StringCalculator {
    private static final int ZERO = 0;

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return ZERO;
        }
        return Arrays
                .stream(text.split(","))
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);
    }
}
