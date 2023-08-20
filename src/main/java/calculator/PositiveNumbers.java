package calculator;

import java.util.List;

public class PositiveNumbers {

    private List<PositiveNumber> numbers;

    public PositiveNumbers(PositiveNumber number) {
        this.numbers = List.of(number);
    }

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(PositiveNumber::value)
                .sum();
    }

}
