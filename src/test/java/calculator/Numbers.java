package calculator;

import calculator.calculate.CalculateStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public Number calculate(CalculateStrategy strategy) {
        return this.numbers.stream()
                .reduce(strategy::calculate)
                .orElse(PositiveNumber.ZERO);
    }
}
