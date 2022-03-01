package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        return Arrays.stream(text.split(","))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}