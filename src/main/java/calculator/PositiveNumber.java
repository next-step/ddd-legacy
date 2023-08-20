package calculator;

import java.util.Objects;

public class PositiveNumber {

    public static final PositiveNumber ZERO = new PositiveNumber(0);
    private final int value;

    public PositiveNumber(int value) {
        this.value = value;
    }

    public PositiveNumber(String value) {
        int parsedValue = Integer.parseInt(value);
        if (parsedValue < 0) {
            throw new RuntimeException();
        }
        this.value = parsedValue;
    }

    public int getValue() {
        return value;
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return new PositiveNumber(value + positiveNumber.getValue());
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
