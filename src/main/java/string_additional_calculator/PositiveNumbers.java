package string_additional_calculator;

import java.util.List;
import java.util.Objects;

public class PositiveNumbers {
    private final List<PositiveNumber> values;

    public PositiveNumbers(List<PositiveNumber> values) {
        this.values = values;
    }

    public static PositiveNumbers of(List<String> stringNumbers) {
        return new PositiveNumbers(stringNumbers.stream()
                .map(PositiveNumber::from)
                .collect(java.util.stream.Collectors.toList()));
    }

    public PositiveNumber totalSum() {
        return this.values.stream()
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum);
    }

    public List<PositiveNumber> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveNumbers that = (PositiveNumbers) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
