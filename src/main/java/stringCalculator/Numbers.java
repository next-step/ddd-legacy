package stringCalculator;

import java.util.ArrayList;
import java.util.List;

public class Numbers {
    private final List<Integer> numbers;

    private Numbers(List<Integer> numbers) {
        this.numbers = new ArrayList<>(numbers);
    }

    public static Numbers from(List<String> numbers) {
        try {
            return new Numbers(numbers.stream().map(Integer::parseInt).toList());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format");
        }
    }

    public static Numbers empty() {
        return new Numbers(new ArrayList<>());
    }

    public boolean isContainMinus() {
        return numbers.stream().anyMatch(number -> number < 0);
    }

    public boolean emptyOrNull() {
        return numbers.isEmpty();
    }

    public Integer sum() {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}

