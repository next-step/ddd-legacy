package calculator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PositiveNumbers {
    public static PositiveNumbers of(final List<Integer> values) {
        final List<PositiveNumber> positiveNumbers = values.stream()
                .map(PositiveNumber::new)
                .collect(Collectors.toList());

        return new PositiveNumbers(positiveNumbers);
    }

    private final List<PositiveNumber> values;

    public PositiveNumbers(final List<PositiveNumber> values) {
        this.values = values;
    }

    public int sum() {
        return values.stream()
                .mapToInt(PositiveNumber::getValue)
                .sum();
    }

    public List<PositiveNumber> getValues() {
        return values;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PositiveNumbers that = (PositiveNumbers) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
