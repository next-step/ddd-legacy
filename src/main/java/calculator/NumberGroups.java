package calculator;

import java.util.Arrays;
import java.util.List;

public class NumberGroups {
    private final List<Number> numbers;

    public NumberGroups(String[] values) {
        this.numbers = Arrays.stream(values)
                .map(Number::from)
                .toList();
    }

    public Number sum() {
        return numbers.stream()
                .reduce(Number.ZERO, Number::add);
    }
}
