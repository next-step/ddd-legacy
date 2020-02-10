package calculator;

import java.util.List;

public class PositiveNumbers {

    private List<PositiveNumber> numbers;

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int getTotal() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }
}
