package calculator.source;

import java.util.Objects;

public class Number {
    private static final int MIN_VALUE = 0;
    private final int value;

    private void validateRange(final int value) {
        if (value < MIN_VALUE) {
            throw new RuntimeException("음수가 아닌 숫자를 입력하세요.");
        }
    }

    public Number(final int input) {
        validateRange(input);
        this.value = input;
    }

    public Number(final String input) {
        this(Integer.parseInt(input));
    }

    public Number plus(final Number number) {
        return new Number(number.value + this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number = (Number) o;
        return value == number.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}

