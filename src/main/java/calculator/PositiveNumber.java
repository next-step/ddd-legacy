package calculator;

import java.util.Objects;

public class PositiveNumber {

    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int value;

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
        this.value = value;
    }

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public int getValue() {
        return value;
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return new PositiveNumber(value + positiveNumber.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveNumber that = (PositiveNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
