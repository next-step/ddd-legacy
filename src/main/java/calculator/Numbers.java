package calculator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Geonguk Han
 * @since 2020-02-08
 */
public class Numbers {

    private List<String> numbers;

    public Numbers(List<String> numbers) {
        this.numbers = numbers;
    }

    protected void validateNumber() {
        if (isNegative()) {
            throw new RuntimeException("There are must be positive numbers");
        }
    }

    private boolean isNegative() {
        return numbers.stream()
                .mapToInt(Integer::parseInt)
                .anyMatch(value -> value < 0);
    }

    protected int sum() {
        return numbers.stream()
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
