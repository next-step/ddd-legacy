package calculator;

import java.util.Arrays;
import java.util.Objects;

public class Calculator {

    public static final int DEFAULT_VALUE = 0;

    public static int sum(final String input) {
        if (Objects.isNull(input) || input.isBlank()) {
            return DEFAULT_VALUE;
        }

        String[] numbers = input.split(",|:");
        return Arrays.stream(numbers)
                .mapToInt(value -> Integer.parseInt(value))
                .sum();
    }
}
