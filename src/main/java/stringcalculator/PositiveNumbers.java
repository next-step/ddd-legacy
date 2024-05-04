package stringcalculator;

import java.util.Arrays;
import java.util.List;

public class PositiveNumbers {

    private final List<PositiveNumber> positives;

    public PositiveNumbers(final List<PositiveNumber> numbers) {
        this.positives = numbers;
    }

    public static PositiveNumbers of(String... numbers) {
        return new PositiveNumbers(Arrays.stream(numbers)
                .map(PositiveNumber::byString)
                .toList());
    }

    public int sum() {
        return positives.stream()
                .map(PositiveNumber::value)
                .reduce(0, Integer::sum);
    }
}
