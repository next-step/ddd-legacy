package string_additional_calculator;

import java.util.Objects;

public class PositiveNumber {
    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private static final int MIN_VALUE = 0;

    private final int value;

    private PositiveNumber(int value) {
        if (isNegativeNumber(value)) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 음수가 될 수 없습니다. number: %s", value));
        }
        this.value = value;
    }

    private static boolean isNegativeNumber(int value) {
        return value < MIN_VALUE;
    }

    public static PositiveNumber from(String stringNumber) {
        validate(stringNumber);
        return new PositiveNumber(Integer.parseInt(stringNumber));
    }

    private static void validate(String stringNumber) {
        try {
            Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException(String.format("문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: %s", stringNumber));
        }
    }

    public PositiveNumber sum(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.value);
    }

    public int getValue() {
        return this.value;
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
