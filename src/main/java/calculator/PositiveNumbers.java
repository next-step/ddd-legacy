package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    public PositiveNumbers(String[] tokens) {
        this(Arrays.stream(tokens)
                .map(PositiveNumber::new)
                .toList());
    }

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositiveNumbers that = (PositiveNumbers) o;
        return Objects.equals(numbers, that.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }
}
