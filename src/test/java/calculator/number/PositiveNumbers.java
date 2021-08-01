package calculator.number;

import calculator.calculate.CalculateStrategy;
import calculator.tokenizer.Tokenizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers implements Numbers{

    private static final Numbers ZERO = new PositiveNumbers();

    private final List<Number> numbers;

    public PositiveNumbers() {
        this.numbers = Collections.emptyList();
    }

    public PositiveNumbers(String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public static Numbers of(Tokenizer tokenizer) {
        return new PositiveNumbers(tokenizer.split());
    }

    public static Numbers of(final String text, final Tokenizer tokenizer) {
        if (isEmptyOfNull(text)) {
            return PositiveNumbers.ZERO;
        }
        return PositiveNumbers.of(tokenizer);
    }

    public Number calculate(final CalculateStrategy strategy) {
        return this.numbers.stream()
                .reduce(PositiveNumber.ZERO, strategy::calculate);
    }

    private static boolean isEmptyOfNull(String text) {
        return text == null || text.isEmpty();
    }
}
