package calculator;

import java.util.List;

public class Numbers {

    private final List<PositiveNumber> numbers;

    public Numbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getNumber)
            .sum();
    }
}
