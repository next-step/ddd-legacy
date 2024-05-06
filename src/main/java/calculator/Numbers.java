package calculator;

import java.util.Arrays;
import java.util.List;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(Number::of)
                .toList();
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(Number::getNumber)
                .sum();
    }
}