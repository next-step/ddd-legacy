package calculator;

import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public static PositiveNumbers fromString(List<String> strings) {
        List<PositiveNumber> numbers = strings.stream()
                .map(PositiveNumber::fromString)
                .collect(Collectors.toList());
        return new PositiveNumbers(numbers);
    }

    public int sum() {
        return numbers.stream()
                .reduce(new PositiveNumber(0), PositiveNumber::plus)
                .getValue();
    }
}
