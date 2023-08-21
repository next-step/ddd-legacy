package calculator;

import java.util.Objects;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수를 입력함");
        }

        this.number = number;
    }

    public PositiveNumber(String stringNumber) {
        if (!isPositiveNumber(stringNumber)) {
            throw new IllegalArgumentException("음수를 입력함");
        }

        try {
            this.number = Integer.parseInt(stringNumber.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아님");
        }
    }

    private boolean isPositiveNumber(String stringNumber) {
        return !stringNumber.startsWith("-");
    }

    public PositiveNumber plus(PositiveNumber other) {
        return new PositiveNumber(this.number + other.number);
    }

    public int getNumber() {
        return this.number;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PositiveNumber positiveNumber = (PositiveNumber) o;
        return this.number == positiveNumber.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }
}
