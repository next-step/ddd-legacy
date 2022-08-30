package calculator.domain;

import java.util.Objects;

public class AddNumber {

    private static final AddNumber ZERO = new AddNumber(0);
    private final int value;

    public AddNumber(int value) {
        this.value = value;
        validateNegative(this.value);
    }

    private void validateNegative(int value) {
        if (value < 0) {
            throw new RuntimeException("덧셈 값은 음수일 수 없습니다.");
        }
    }

    public static AddNumber from(String value) {
        try {
            return convertToAddNumber(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("정수 외 값은 덧셈 값이 될 수 없습니다.");
        }
    }

    private static AddNumber convertToAddNumber(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            return ZERO;
        }

        return new AddNumber(Integer.parseInt(value));
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddNumber addNumber = (AddNumber) o;
        return value == addNumber.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
