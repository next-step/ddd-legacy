package common.numeric;

import java.util.Objects;

public class NonNegativeNumber {

    private final int number;

    public NonNegativeNumber(final String input) {
        final int number = parseInt(input);
        validateNonNegative(number);
        this.number = number;
    }

    public NonNegativeNumber(final int number) {
        validateNonNegative(number);
        this.number = number;
    }

    private void validateNonNegative(int number) {
        if (number < 0) {
            throw new RuntimeException();
        }
    }

    private int parseInt(final String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    public NonNegativeNumber add(final NonNegativeNumber other) {
        return new NonNegativeNumber(this.number + other.number);
    }

    public int getInt() {
        return this.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NonNegativeNumber)) {
            return false;
        }
        NonNegativeNumber that = (NonNegativeNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
