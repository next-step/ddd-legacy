package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private List<Integer> numbers;

    private Numbers(String[] numbers) {
        this.numbers = Arrays.asList(numbers)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static Numbers of(String[] numbers) {
        return new Numbers(numbers);
    }

    public boolean hasAnyNegativeNumber() {
        return numbers.stream()
                .anyMatch(n -> n < ZERO);
    }

    public boolean hasOnlyOneNumber() {
        return numbers.size() == ONE;
    }

    public int addAll() {
        return numbers.stream()
                .reduce(ZERO, Integer::sum);
    }
}
