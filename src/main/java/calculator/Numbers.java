package calculator;

import java.util.Arrays;
import java.util.List;

public class Numbers {
    static final Numbers ZERO_NUMBERS = new Numbers(List.of(Number.ZERO_NUMBER));
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
}
