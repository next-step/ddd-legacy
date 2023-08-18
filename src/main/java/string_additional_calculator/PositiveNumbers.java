package string_additional_calculator;

import java.util.List;

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
}
