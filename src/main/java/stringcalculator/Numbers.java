package stringcalculator;

import java.util.Collections;
import java.util.List;

public class Numbers {

    private final List<Number> numbers;

    public Numbers(List<Number> numbers) {
        this.numbers = numbers;
    }

    public Numbers(Number number) {
        this.numbers = Collections.singletonList(number);
    }

    public int sum() {
        return numbers.stream().mapToInt(Number::getValue).sum();
    }
}
