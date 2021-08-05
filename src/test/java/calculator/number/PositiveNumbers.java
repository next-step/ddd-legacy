package calculator.number;

import calculator.calculate.CalculateStrategy;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers implements Numbers{

    private final List<Number> numbers;

    public PositiveNumbers(final String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public static PositiveNumbers of(final String[] tokens) {
        return new PositiveNumbers(tokens);
    }

    public Number calculate(final CalculateStrategy strategy) {
        return this.numbers.stream()
                .reduce(PositiveNumber.ZERO, strategy::calculate);
    }
}
