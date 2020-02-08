package calculator;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class PositiveNumbers {
    static final PositiveNumbers EMPTY = from();
    private final List<PositiveNumber> positiveNumbers;

    static PositiveNumbers from(int... positiveNumbers) {
        return new PositiveNumbers(Arrays.stream(positiveNumbers)
                                         .mapToObj(PositiveNumber::new)
                                         .collect(toList()));
    }

    PositiveNumbers(List<PositiveNumber> positiveNumbers) {
        if (positiveNumbers == null) { throw new IllegalArgumentException(); }
        this.positiveNumbers = Collections.unmodifiableList(positiveNumbers);
    }

    int sum() {
        return positiveNumbers.stream()
                              .reduce(PositiveNumber::sum)
                              .orElse(PositiveNumber.ZERO).val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PositiveNumbers that = (PositiveNumbers) o;
        return positiveNumbers.equals(that.positiveNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positiveNumbers);
    }
}
