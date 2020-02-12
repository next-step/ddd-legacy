package calculator;

import java.util.List;

public class PositiveIntegers {
    private final List<PositiveInteger> numbers;

    public PositiveIntegers(List<PositiveInteger> numbers) {
        this.numbers = numbers;
    }

    public void add(PositiveInteger positiveInteger) {
        numbers.add(positiveInteger);
    }

    public Integer sum() {
        return numbers.stream()
                .mapToInt(PositiveInteger::valueOf)
                .reduce((a, b) -> a + b)
                .orElse(-1);
    }

    public Integer multiply() {
        return numbers.stream()
                .mapToInt(PositiveInteger::valueOf)
                .reduce((a, b) -> a * b)
                .orElse(-1);
    }

}
