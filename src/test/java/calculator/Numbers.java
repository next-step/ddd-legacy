package calculator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Numbers {
    private final List<Number> numbers;

    public Numbers(String[] numbers) {
        this.numbers = Stream.of(numbers)
                .map(Number::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        return this.numbers.stream()
                .mapToInt(Number::intValue)
                .sum();
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
