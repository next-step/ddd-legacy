package calculator;

import calculator.exception.IllegalNumberException;

import java.util.Objects;

public class Number {
    private static final int ZERO = 0;
    static final Number ZERO_NUMBER = new Number(ZERO);
    static final String PARSING_INTEGER_EXCEPTION = "숫자로 변환할 수 없는 문자열 입니다.";
    static final String NEGATIVE_NUMBER_EXCEPTION = "음수는 유효하지 않은 숫자입니다.";

    private final int value;

    private Number(final int value) {
        validateNumber(value);
        this.value = value;
    }

    public static Number from(final String value) {
        if (isNullOrBlank(value) || isZeroValue(value)) {
            return ZERO_NUMBER;
        }
        return new Number(parseInt(value));
    }

    public Number plus(final Number number) {
        return new Number(this.value + number.value);
    }

    public int value() {
        return value;
    }

    private static void validateNumber(final int value) {
        if (value < ZERO) {
            throw new IllegalNumberException(NEGATIVE_NUMBER_EXCEPTION, String.valueOf(value));
        }
    }

    private static int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException(PARSING_INTEGER_EXCEPTION, value);
        }
    }

    private static boolean isNullOrBlank(final String value) {
        return value == null || value.isBlank();
    }

    private static boolean isZeroValue(String value) {
        return value.equals("0");
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
