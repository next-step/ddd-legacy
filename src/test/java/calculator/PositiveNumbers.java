package calculator;

import java.util.List;

public final class PositiveNumbers {

    private final List<PositiveNumber> values;

    public PositiveNumbers(List<PositiveNumber> values) {
        this.values = values;
    }

    public int sum() {
        return values.stream()
                .mapToInt(PositiveNumber::getValue)
                .sum();
    }
}
