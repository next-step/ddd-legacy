package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PositiveNumbers {

    private final List<PositiveNumber> numbers;

    private PositiveNumbers(final List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public static PositiveNumbers of(final Seperator seperator) {
        return of(seperator.getTargetNumber(), seperator.getDelimiter());
    }

    public static PositiveNumbers of(final String targetNumber, final String delimiter) {
        final List<PositiveNumber> numbers = Arrays.stream(targetNumber.split(delimiter))
            .map(PositiveNumber::of)
            .collect(Collectors.toList());
        return new PositiveNumbers(numbers);
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PositiveNumbers numbers1 = (PositiveNumbers) o;
        return Objects.equals(numbers, numbers1.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }
}
