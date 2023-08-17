package calculator;

import java.util.Objects;

public class PositiveInteger {
    private final int value;

    public PositiveInteger(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수를 사용할 수 없습니다.");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public PositiveInteger sum(final PositiveInteger rightHand) {
        return new PositiveInteger(this.value + rightHand.value);
    }

    public static PositiveInteger zero() {
        return new PositiveInteger(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveInteger that = (PositiveInteger) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
