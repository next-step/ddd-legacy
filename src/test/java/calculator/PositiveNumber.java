package calculator;

import java.util.Objects;

public final class PositiveNumber {

    private static final String NOT_POSITIVE = "음수를 입력할 수 없습니다.";
    private static final String NOT_A_NUMBER = "숫자가 아닌 값을 입력 할 수 없습니다.";
    private static final int MIN = 0;

    private final int value;

    public PositiveNumber(String value) {
        if (value.isEmpty()) {
            this.value = MIN;
            return;
        }

        int number = parseInt(value);

        validate(number);
        this.value = number;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(NOT_A_NUMBER);
        }
    }

    private void validate(int value) {
        if (value < MIN) {
            throw new RuntimeException(NOT_POSITIVE);
        }
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveNumber positiveNumber = (PositiveNumber) o;
        return value == positiveNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
