package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            // not a single number? continue
        }

        return Arrays.stream(text.split("[,:]"))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
