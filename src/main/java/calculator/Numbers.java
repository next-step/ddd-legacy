package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Numbers {
    public static final Numbers ZERO_NUMBERS = new Numbers(List.of(Number.ZERO_NUMBER));

    private final List<Number> values;

    public Numbers(String... stringNumbers) {
        this(Arrays.stream(stringNumbers)
                .map(Number::from)
                .toList());
    }

    public Numbers(List<Number> values) {
        this.values = values;
    }

    public Number sum() {
        return values.stream()
                .reduce(Number::plus)
                .orElse(Number.ZERO_NUMBER);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Numbers numbers = (Numbers) o;
        return Objects.equals(values, numbers.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
