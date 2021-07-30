package study1;

import java.util.Objects;

public class PositiveNumber {

    private final int value;

    private PositiveNumber(final int value) {
        checkPositive(value);
        this.value = value;
    }

    public static PositiveNumber valueOf(final String value) {
        return new PositiveNumber(Integer.parseInt(value));
    }

    private void checkPositive(final int value) {
        if (value < 0) {
            throw new RuntimeException("음수 값은 허용되지 않습니다.");
        }
    }

    public PositiveNumber add(final PositiveNumber number) {
        return new PositiveNumber(value + number.value);
    }

    public int value() {
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
        return Objects.hash(value);
    }
}
