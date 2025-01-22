package calculator;

import java.util.List;

public class PositiveIntegers {
    private final List<PositiveInteger> values;

    public PositiveIntegers(List<PositiveInteger> values) {
        this.values = values;
    }

    public PositiveIntegers(PositiveInteger positiveInteger) {
        this.values = List.of(positiveInteger);
    }

    public List<Integer> intValues() {
        return values.stream()
                .map(PositiveInteger::toInt)
                .toList();
    }
}
