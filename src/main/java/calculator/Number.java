package calculator;

import calculator.exception.InvalidNumberFormatException;
import calculator.exception.NegativeNumberException;

import java.util.Objects;

public class Number {
    private static final int ZERO = 0;
    static final Number ZERO_NUMBER = new Number(ZERO);

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
            throw new NegativeNumberException(value);
        }
    }

    private static int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException(value);
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
