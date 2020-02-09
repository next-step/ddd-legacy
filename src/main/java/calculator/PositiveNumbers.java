package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {

    private List<PositiveNumber> numbers;

    public PositiveNumbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
            .map(PositiveNumber::of)
            .collect(Collectors.toList());
    }

    public int getTotal() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }
}
