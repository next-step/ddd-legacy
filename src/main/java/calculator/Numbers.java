package calculator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Numbers {

    private final List<Number> numbers;

    private Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static Numbers from(List<String> rawNumbers) {
        List<Number> numbers = rawNumbers.stream()
                .map(Number::from)
                .collect(Collectors.toList());
        return new Numbers(numbers);
    }

    public static Numbers from(String... rawNumber) {
        return from(List.of(rawNumber));
    }

    public int sum() {
        return numbers.stream()
                .reduce(Number.ZERO, Number::plus)
                .getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Numbers numbers1 = (Numbers) o;
        return Objects.equals(numbers, numbers1.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }
}
