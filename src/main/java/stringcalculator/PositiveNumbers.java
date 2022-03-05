package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PositiveNumbers {

    private final List<PositiveNumber> elements;

    public PositiveNumbers(List<PositiveNumber> elements) {
        this.elements = elements;
    }

    public PositiveNumbers(PositiveNumber... elements) {
        this(Arrays.asList(elements));
    }

    public PositiveNumbers() {
        this(Collections.emptyList());
    }

    public PositiveNumber sum() {
        return elements.stream()
            .reduce(PositiveNumber.ZERO, PositiveNumber::plus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositiveNumbers that = (PositiveNumbers) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
