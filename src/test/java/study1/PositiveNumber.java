package study1;

import java.util.Objects;

public class PositiveNumber {

    private final int value;

    public PositiveNumber(final String value) {
        this(Integer.parseInt(value));
    }

    private PositiveNumber(final int value) {
        checkPositive(value);
        this.value = value;
    }

    private void checkPositive(final int value) {
        if (value < 0) {
            throw new RuntimeException();
        }
    }

    public PositiveNumber add(final PositiveNumber number) {
        return new PositiveNumber(value + number.value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PositiveNumber number = (PositiveNumber) o;
        return value == number.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
