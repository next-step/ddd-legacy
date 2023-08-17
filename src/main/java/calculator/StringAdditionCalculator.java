package calculator;

import java.util.Arrays;

public class StringAdditionCalculator {
    private final static String DELIMITER = "[,:]";

    public static int calculate(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        return Arrays.stream(input.split(DELIMITER))
                     .mapToInt(Integer::parseInt)
                     .sum();
    }
}
