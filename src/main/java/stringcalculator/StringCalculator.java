package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            String[] values = text.split(",");
            return Arrays.stream(values).mapToInt(Integer::parseInt).sum();
        }
    }
}
