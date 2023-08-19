package stringcalculator;

import java.util.Objects;

public class Number {
    private static final int MIN_NUMBER_VALUE = 0;
    private final Integer value;

    private Number(Integer value) {
        validate(value);
        this.value = value;
    }

    public static Number of(int value) {
        return new Number(value);
    }

    private void validate(int number) {
        if (number < MIN_NUMBER_VALUE) {
            throw new RuntimeException("음수를 입력할 수 없습니다.");
        }
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number = (Number) o;
        return Objects.equals(value, number.value);
    }
}
