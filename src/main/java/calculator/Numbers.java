package calculator;

import java.util.Arrays;
import java.util.List;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(String[] tokens) {
        this(Arrays.stream(tokens)
                .map(Number::new)
                .toList());
    }

    public Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public int getSum() {
        return numbers.stream()
                .mapToInt(Number::getNumber)
                .sum();
    }
}
