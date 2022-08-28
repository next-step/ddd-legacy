package calculator;

import java.util.Arrays;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] numbers = text.split(",|:");

        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
