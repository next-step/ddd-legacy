package stringcalculator.number;

import java.util.Objects;

public class Number {

    private static final int THRESHOLD_VALUE = 0;
    private static final String POSITIVE_NUMBER_ERROR_MESSAGE = "value must be positive number";

    private final int value;

    public Number(String value) {
        this(Integer.parseInt(value));
    }

    protected Number(int value) {
        validateValue(value);
        this.value = value;
    }

    private void validateValue(int value) {
        if (value < THRESHOLD_VALUE) {
            throw new RuntimeException(POSITIVE_NUMBER_ERROR_MESSAGE);
        }
    }

    public Number sum(Number other) {
        return new Number(Integer.sum(other.getValue(), value));
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
