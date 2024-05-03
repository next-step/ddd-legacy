package calculator.domain;

import java.util.Objects;

public class Number {
    private final int number;

    public Number() {
        this(0);
    }

    public Number(String number) {
        this(Integer.valueOf(number));
    }

    public Number(Integer number) {
        handleNegative(number);
        this.number = number;
    }

    private void handleNegative(Integer number) {
        if (number < 0) {
            throw new RuntimeException("전달된 수는 음수입니다. : " + number);
        }
    }

    public Number add(Number number) {
        int result = this.number + number.number;
        return new Number(result);
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
        return Objects.hash(number);
    }
}
