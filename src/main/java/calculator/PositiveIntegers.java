package calculator;

import java.util.List;

public class PositiveIntegers {
    private final List<PositiveInteger> integers;

    public PositiveIntegers(List<PositiveInteger> integers) {
        this.integers = integers;
    }

    public PositiveInteger sumAll() {
        return integers.stream().reduce(PositiveInteger.zero(), PositiveInteger::sum);
    }

    public PositiveInteger get(int index) { return integers.get(index); }
}
