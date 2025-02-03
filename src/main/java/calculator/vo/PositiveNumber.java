package calculator.vo;

import calculator.exception.ErrorCode;
import calculator.exception.NegativeNumberException;

public record PositiveNumber(int value) {
    public PositiveNumber {
        if (value < 0) {
            throw new NegativeNumberException(ErrorCode.NEGATIVE_NOT_ALLOWED.toString());
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PositiveNumber that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
