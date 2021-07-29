package calculator;

import java.util.Objects;

public class PositiveNumber {

    public static final int ZERO = 0;
    public static final String NEGATIVE_EXCEPTION_MESSAGE = "0 이상의 값을 넣어주세요";
    public static final String NOT_NUMBER_EXCEPTION_MESSAGE = "0 이상의 숫자를 넣어주세요";
    private final int value;

    public PositiveNumber(final String value) {
        final int number = parseToInt(value);
        checkNegativenumber(number);
        this.value = number;
    }

    private int parseToInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            throw new RuntimeException(NOT_NUMBER_EXCEPTION_MESSAGE);
        }
    }

    private void checkNegativenumber(final int number) {
        if (number < ZERO) {
            throw new RuntimeException(NEGATIVE_EXCEPTION_MESSAGE);
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PositiveNumber that = (PositiveNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
