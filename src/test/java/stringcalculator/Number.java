package stringcalculator;

import java.util.Objects;

public class Number {

    public static final Number ZERO = new Number(0);

    private static final int AT_LEAST_POSITIVE_VALUE = 0;
    private static final String MUST_BE_POSITIVE_NUMBER = "value must be positive number";

    private final int value;

    public Number(String value) {
        this(Integer.parseInt(value));
    }

    private Number(int value) {
        validateValue(value);
        this.value = value;
    }

    public static Number sum(Number a, Number b) {
        return new Number(Integer.sum(a.value, b.value));
    }

    private void validateValue(int value) {
        if (value < AT_LEAST_POSITIVE_VALUE) {
            throw new RuntimeException(MUST_BE_POSITIVE_NUMBER);
        }
    }

    public int getValue() {
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
