package calculator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static Numbers fromString(List<String> strings) {
        List<Number> numbers = strings.stream()
                .map(Number::fromString)
                .collect(Collectors.toList());
        return new Numbers(numbers);
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