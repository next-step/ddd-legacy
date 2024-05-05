package calculator;

import java.util.List;

public class Numbers {
    private final List<Integer> numbers;

    public Numbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
}
