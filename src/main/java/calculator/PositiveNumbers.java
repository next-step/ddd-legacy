package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final List<Integer> numbers) {
        if (numbers != null) {
            this.numbers = numbers.stream()
                    .map(PositiveNumber::new)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            this.numbers = new ArrayList<>();
        }
    }

    public PositiveNumber sum() {
        return this.numbers
                .stream()
                .reduce(PositiveNumber::plus)
                .orElse(new PositiveNumber(0));
    }
}
