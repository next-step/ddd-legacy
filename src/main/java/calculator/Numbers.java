package calculator;

import static calculator.ValidateUtils.checkNotEmpty;

import java.util.List;

public final class Numbers {

    private final List<Number> numbers;

    public Numbers(final List<Number> numbers) {
        this.numbers = checkNotEmpty(numbers, "numbers");
    }

    public void checkHasNegativeInt() {
        numbers.forEach(Number::checkIsNegative);
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(Number::getValue)
            .sum();
    }
}
