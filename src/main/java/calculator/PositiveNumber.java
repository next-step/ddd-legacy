package calculator;

import java.util.Objects;

class PositiveNumber {
    static final PositiveNumber ZERO = new PositiveNumber(0);
    final int val;

    static PositiveNumber from(String positiveNumber) {
        return new PositiveNumber(Integer.parseInt(positiveNumber));
    }

    PositiveNumber(int positiveNumber) {
        if (positiveNumber < 0) { throw new IllegalArgumentException(); }
        this.val = positiveNumber;
    }

    PositiveNumber sum(PositiveNumber positiveNumber) {
        if (positiveNumber == null) { throw new IllegalArgumentException(); }
        return new PositiveNumber(this.val + positiveNumber.val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PositiveNumber that = (PositiveNumber) o;
        return val == that.val;
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }
}
