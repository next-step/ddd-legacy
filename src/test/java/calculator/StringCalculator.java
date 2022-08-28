package calculator;

import java.util.Arrays;

public class StringCalculator {

    private static final String DELIMITER_REGEX = ",|:";

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] numbers = text.split(DELIMITER_REGEX);

        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
