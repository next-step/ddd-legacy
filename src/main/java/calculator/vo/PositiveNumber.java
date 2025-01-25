package calculator.vo;

import calculator.exception.ErrorCode;

public record PositiveNumber(int value) {
    public PositiveNumber {
        if (value < 0) {
            throw new RuntimeException(ErrorCode.NEGATIVE_NOT_ALLOWED.toString());
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
