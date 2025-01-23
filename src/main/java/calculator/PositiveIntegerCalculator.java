package calculator;

import java.util.List;

public class PositiveIntegerCalculator {
    private final List<PositiveInteger> values;

    public PositiveIntegerCalculator(List<PositiveInteger> values) {
        this.values = values;
    }

    public PositiveIntegerCalculator(PositiveInteger positiveInteger) {
        this.values = List.of(positiveInteger);
    }

    public int sum() {
        return values.stream()
                .map(PositiveInteger::toInt)
                .reduce(0, Integer::sum);
    }
}
