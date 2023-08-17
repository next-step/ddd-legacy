package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final String[] stringNumbers) {
        this.numbers = Arrays.stream(stringNumbers)
                .filter(stringNumber -> !stringNumber.isEmpty())
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    public PositiveNumber sum() {
        return this.numbers
                .stream()
                .reduce(PositiveNumber::plus)
                .orElse(new PositiveNumber(0));
    }
}
