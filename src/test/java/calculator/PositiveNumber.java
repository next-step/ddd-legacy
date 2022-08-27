package calculator;

import java.util.Objects;

public class PositiveNumber {
    private static final int ZERO = 0;
    private static final int MIN_NUMBER = ZERO;

    private int number;

    public PositiveNumber(String number) {
        this(Integer.parseInt(number));
    }

    public PositiveNumber(int number) {
        if (number < MIN_NUMBER) {
            throw new IllegalArgumentException("계산할 숫자는 음수를 가질수 없습니다.");
        }
        this.number = number;
    }

    public static PositiveNumber zeroNumber() {
        return new PositiveNumber(ZERO);
    }

    public void add(PositiveNumber addNumber) {
        this.number += addNumber.number;
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
        return getNumber() == that.getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber());
    }
}
