package calculator;

import java.util.List;

public class PositiveIntegers {
    private final List<PositiveInteger> values;

    public PositiveIntegers(List<PositiveInteger> values) {
        this.values = values;
    }

    public static PositiveIntegers of(String str) {
        InputValue inputValue = InputValue.of(str);
        return new PositiveIntegers(inputValue.getPositiveIntegers());
    }

    public List<Integer> intValues() {
        return values.stream()
                .map(PositiveInteger::toInt)
                .toList();
    }
}
