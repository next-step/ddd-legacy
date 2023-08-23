package calculator;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public final class PositiveNumber {

    private final int value;

    public PositiveNumber(final int value) {
        if (value < 0) {
            throw new RuntimeException(
                String.format("it is negative value: %s", value));
        }

        this.value = value;
    }

    PositiveNumber sum(final PositiveNumber number) {
        return new PositiveNumber(number.value + value);
    }

    int getValue() {
        return value;
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
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("value", value)
            .toString();
    }
}
