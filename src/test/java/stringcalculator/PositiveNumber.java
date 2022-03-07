package stringcalculator;

import java.util.Objects;

public class PositiveNumber {

    public static final int ZERO = 0;

    private final int number;

    public PositiveNumber(String value) {
        this.number = parseNumber(value);
    }

    private PositiveNumber(int number) {
        validateNumber(number);
        this.number = number;
    }

    public static PositiveNumber zero() {
        return new PositiveNumber(0);
    }

    private void validateNumber(int number) {
        if (number < ZERO) {
            throw new IllegalArgumentException("0 보다 작은 값은 더할 수 없습니다.");
        }
    }

    private int parseNumber(String value) {
        try {
            if (Integer.parseInt(value) < 0) {
                throw new IllegalArgumentException("0 보다 작은 값은 더할 수 없습니다.");
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("숫자만 입력 가능합니다.");
        }
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(number + other.number);
    }

    public int getValue() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveNumber that = (PositiveNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
