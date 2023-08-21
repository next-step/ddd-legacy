package calculator;

import java.util.Objects;

public class PositiveNumber {
    public static final PositiveNumber ZERO = new PositiveNumber(0);

    private final int value;

    public PositiveNumber(final int value) {
        if (value < 0) {
            throw new RuntimeException();
        }

        this.value = value;
    }

    public PositiveNumber plus(final PositiveNumber positiveNumber) {
        return new PositiveNumber(
                this.value + positiveNumber.value
        );
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PositiveNumber that = (PositiveNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
