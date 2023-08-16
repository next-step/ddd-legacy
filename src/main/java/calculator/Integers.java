package calculator;

import java.util.List;

public class Integers {
    private final List<Integer> integers;

    public Integers(List<Integer> integers) {
        this.integers = integers;
    }

    public int sum() {
        return integers.stream().reduce(0, Integer::sum);
    }
}
