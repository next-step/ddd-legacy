package calculator;

import java.util.Objects;

public class Number {
    private final int value;

    public Number(String value) {
        this.value = parseInt(value);
        validateAmniotic();
    }

    private void validateAmniotic() {
        if (this.value < 0) {
            throw new RuntimeException("양수만 계산이 가능합니다.");
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new RuntimeException("숫자만 계산이 가능합니다.");
        }
    }

    public int intValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number = (Number) o;
        return value == number.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
