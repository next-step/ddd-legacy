package calculator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class PositiveNumbers {
    static final PositiveNumbers EMPTY = new PositiveNumbers(Collections.emptyList());
    private final List<PositiveNumber> numbers;

    PositiveNumbers(List<PositiveNumber> numbers) {
        if (numbers == null) { throw new IllegalArgumentException(); }
        this.numbers = Collections.unmodifiableList(numbers);
    }

    int sum() {
        return numbers.stream()
                      .reduce(PositiveNumber::sum)
                      .orElse(PositiveNumber.ZERO).val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PositiveNumbers that = (PositiveNumbers) o;
        return numbers.equals(that.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }
}
