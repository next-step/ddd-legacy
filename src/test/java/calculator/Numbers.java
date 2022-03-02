package calculator;

import java.util.List;

public final class Numbers {

    private final List<Number> values;

    public Numbers(List<Number> values) {
        this.values = values;
    }

    public int sum() {
        return values.stream()
                .mapToInt(Number::getValue)
                .sum();
    }
}
