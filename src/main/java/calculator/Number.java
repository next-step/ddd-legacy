package calculator;

import java.util.Objects;

public class Number {

    private final int number;

    private Number(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be positive.");
        }
        this.number = number;
    }

    public static Number of(String value) {
        try {
            return new Number(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number1 = (Number) o;
        return number == number1.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}