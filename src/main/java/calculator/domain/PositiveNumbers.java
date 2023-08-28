package calculator.domain;

import java.util.List;

public class PositiveNumbers {
    private List<PositiveNumber> numbers;

    private PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public static PositiveNumbers from(List<PositiveNumber> numbers) {
        return new PositiveNumbers(numbers);
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }
}
