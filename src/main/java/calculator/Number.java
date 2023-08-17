package calculator;

import java.math.BigInteger;
import java.util.Objects;

public class Number {
    private final BigInteger value;

    public Number(BigInteger number) {
        checkingNegative(number);
        this.value = number;
    }

    public static Number of(String text) {
        try {
            return new Number(new BigInteger(text));
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값이 존재 합니다.");
        }
    }

    private void checkingNegative(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) < 0) {
            throw new RuntimeException("숫자가 음수입니다.");
        }
    }

    public Number plus(Number number) {
        return new Number(this.value.add(number.value));
    }

    public int getValue() {
        return value.intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Number))
            return false;
        Number number = (Number)o;
        return this.value.compareTo(number.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
