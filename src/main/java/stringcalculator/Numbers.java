package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Number> numbers;

    public Numbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(Number::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        return numbers.stream().mapToInt(Number::getValue).sum();
    }
}
