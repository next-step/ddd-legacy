package calculator.shared;

import java.util.List;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public static PositiveNumbers from(List<Integer> numbers) {
        return new PositiveNumbers(numbers.stream()
                .map(PositiveNumber::of)
                .toList());
    }

    public int sum() {
        return numbers.stream()
                .reduce(PositiveNumber.ZERO(), PositiveNumber::add)
                .getValue();
    }

    public int size() {
        return numbers.size();
    }

    @Override
    public String toString() {
        return numbers.toString();
    }

    public boolean isEmpty() {
        return numbers.isEmpty();
    }
}
