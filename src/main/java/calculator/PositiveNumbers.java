package calculator;

import java.util.Arrays;
import java.util.List;

public class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(String[] tokens) {
        this(Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .toList());
    }

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }
}
