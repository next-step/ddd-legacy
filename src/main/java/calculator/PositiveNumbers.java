package calculator;

import static calculator.ParameterValidateUtils.checkNotEmpty;

import java.util.List;

public final class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final List<PositiveNumber> numbers) {
        this.numbers = checkNotEmpty(numbers, "numbers");
    }

    public int sum() {
        return numbers.stream()
            .reduce(PositiveNumber::sum)
            .map(PositiveNumber::getValue)
            .orElseThrow(() -> new IllegalStateException("can not reachable state"));
    }
}
