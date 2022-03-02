package stringcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers() {
        this.numbers = new ArrayList<>();
    }

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public int getSum() {
        return numbers.stream()
                .map(PositiveNumber::getValue)
                .reduce(0, Integer::sum);
    }

    public List<PositiveNumber> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }
}
