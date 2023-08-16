package calculator;

import java.util.Objects;

public class Number {
    private static final int ZERO = 0;

    private final int value;

    public Number(int value) {
        if (value < ZERO) {
            throw new IllegalArgumentException("숫자는 음수가 될 수 없습니다");
        }
        this.value = value;
    }

    public static Number fromString(String s) {
        try {
            return new Number(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닌 문자열이 포함되어 있습니다", e);
        }
    }

    public Number plus(Number number) {
        return new Number(this.value + number.value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number that = (Number) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
