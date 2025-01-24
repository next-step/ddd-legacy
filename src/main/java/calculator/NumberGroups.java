package calculator;

import java.util.Arrays;
import java.util.List;

public class NumberGroups {
    private final List<Number> numbers;

    public NumberGroups(String[] values) {
        this.numbers = Arrays.stream(values)
                .map(Number::new)
                .toList();
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(Number::value)
                .sum();
    }
}
