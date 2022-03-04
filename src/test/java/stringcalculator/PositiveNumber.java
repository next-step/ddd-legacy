package stringcalculator;

import java.util.Objects;

public class PositiveNumber {
    private static final String NEGATIVE_NUMBER_NOT_ALLOWED = "음수는 허용되지 않습니다. 입력된 수: ";

    private final int value;

    public PositiveNumber() {
        this(0);
    }

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException(NEGATIVE_NUMBER_NOT_ALLOWED + value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PositiveNumber sum(PositiveNumber p1, PositiveNumber p2) {
        return new PositiveNumber(p1.value + p2.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveNumber)) return false;
        PositiveNumber that = (PositiveNumber) o;
        return getValue() == that.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
