package calculator;

import calculator.exception.NegativeNumberException;
import java.util.Objects;

public class PositiveNumber {

    public static final int ZERO_NUMBER = 0;
    private final int number;

    public PositiveNumber(String token) {
        this(Integer.parseInt(token));
    }

    public PositiveNumber(int number) {
        validateNumber(number);
        this.number = number;
    }

    private void validateNumber(int number) {
        if (!isPositive(number)) {
            throw new NegativeNumberException(number + "is Negative");
        }
    }

    private boolean isPositive(int number) {
        return number >= 0;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositiveNumber that = (PositiveNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
