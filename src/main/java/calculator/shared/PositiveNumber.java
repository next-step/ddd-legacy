package calculator.shared;

import java.util.Objects;

public class PositiveNumber {
    private int value;

    private PositiveNumber(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("0 미만의 음수는 입력할 수 없습니다. 0을 포함한 양수만 입력해주세요. (입력값: %d)".formatted(value));
        }
        this.value = value;
    }

    public static PositiveNumber of(Integer integer) {
        return new PositiveNumber(integer);
    }

    public static PositiveNumber ZERO(){
        return new PositiveNumber(0);
    }

    public int getValue() {
        return value;
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveNumber)) return false;
        PositiveNumber that = (PositiveNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
