package calculator.number;

import calculator.calculate.CalculateStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers implements Numbers{

    private final List<Number> numbers;

    public PositiveNumbers(String[] tokens) {
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
