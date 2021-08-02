package calculator.number;

import calculator.calculate.CalculateStrategy;
import calculator.tokenizer.Tokenizer;
import calculator.tokenizer.TokenizerFactory;

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

    public PositiveNumbers(final String[] tokens) {
        this.numbers = Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public static Numbers of(final Tokenizer tokenizer) {
        return new PositiveNumbers(tokenizer.split());
    }

    public static Numbers of(final String text) {
        final TokenizerFactory tokenizerFactory = new TokenizerFactory(text);
        final Tokenizer tokenizer = tokenizerFactory.createTokenizer();
        return PositiveNumbers.of(tokenizer);
    }

    public Number calculate(final CalculateStrategy strategy) {
        return this.numbers.stream()
                .reduce(PositiveNumber.ZERO, strategy::calculate);
    }
}
