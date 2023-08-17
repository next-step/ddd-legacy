package calculator;

import java.util.Arrays;

public class StringCalculator {
    private final static String DELIMITER = "[,:]";

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        return Arrays.stream(input.split(DELIMITER))
                     .mapToInt(Integer::parseInt)
                     .sum();
    }
}
