package calculator;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public final class Number {

    private final int value;

    public Number(final int value) {
        this.value = value;
    }
    
    public void checkIsNegative() {
        if (value < 0) {
            throw new RuntimeException(
                String.format("it is negative value: %s", value));
        }
    }

    public int getValue() {
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
        final Number number = (Number) o;
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
