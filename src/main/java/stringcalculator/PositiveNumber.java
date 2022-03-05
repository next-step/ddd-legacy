package stringcalculator;

import java.util.Objects;

public final class PositiveNumber {

    public static final PositiveNumber ZERO = new PositiveNumber(0);

    private final int value;

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수로는 생성할 수 없습니다. value: " + value);
        }
        this.value = value;
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(value + other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositiveNumber that = (PositiveNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
