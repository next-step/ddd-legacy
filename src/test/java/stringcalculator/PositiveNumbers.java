package stringcalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PositiveNumbers {
    private final List<PositiveNumber> numbers;

    public PositiveNumbers() {
        this.numbers = new ArrayList<>();
    }

    public PositiveNumbers(List<PositiveNumber> numbers) {
        this.numbers = numbers;
    }

    public PositiveNumber sum() {
        return numbers.stream()
                .reduce(new PositiveNumber(), PositiveNumber::sum);
    }

    public List<PositiveNumber> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveNumbers)) return false;
        PositiveNumbers that = (PositiveNumbers) o;
        return Objects.equals(getNumbers(), that.getNumbers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumbers());
    }
}
