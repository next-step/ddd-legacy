package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers(final String[] stringNumbers) {
        this.numbers = Arrays.stream(stringNumbers)
                .filter(stringNumber -> !isEmptyString(stringNumber))
                .map(PositiveNumber::new)
                .collect(Collectors.toList());
    }

    private boolean isEmptyString(String stringNumber) {
        return stringNumber.length() == 0;
    }

    public int sum() {
        return this.numbers
                .stream()
                .mapToInt(PositiveNumber::getNumber)
                .sum();
    }

    public boolean isEmpty() {
        return this.numbers.isEmpty();
    }
}
