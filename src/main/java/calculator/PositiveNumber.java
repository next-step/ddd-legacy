package calculator;

import java.util.Objects;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(int number) {
        this.number = number;
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
