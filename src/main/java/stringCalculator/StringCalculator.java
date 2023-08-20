package stringCalculator;

import java.util.Arrays;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        if (text.length() == 1) {
            return Integer.parseInt(text);
        }

        return Arrays.stream(text.split(",|:"))
            .mapToInt(Integer::parseInt)
            .sum();
    }

}
