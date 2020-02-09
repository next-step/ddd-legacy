package calculator;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Geonguk Han
 * @since 2020-02-08
 */
public class Number {

    private String[] numbers;

    public Number(String[] numbers) {
        this.numbers = numbers;
    }

    protected void validateNumber() {
        if (isNegative()) {
            throw new RuntimeException("There are must be positive numbers");
        }
    }

    private boolean isNegative() {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .anyMatch(value -> value < 0);
    }

    protected int sum() {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
