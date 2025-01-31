package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Number> values;

    private Numbers(final List<Number> values) {
        this.values = values;
    }

    public static Numbers from(final String[] values) {
        List<Number> numbers = Arrays.stream(values)
                .map(Number::from)
                .collect(Collectors.toList());

        return new Numbers(numbers);
    }

    public int sum() {
        return values.stream()
                .mapToInt(Number::getValue)
                .sum();
    }

    public List<Number> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }
}
