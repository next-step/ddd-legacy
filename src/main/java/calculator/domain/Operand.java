package calculator.domain;

import java.util.Objects;

public class Operand {

    private static final Operand ZERO = new Operand(0);
    private final int value;

    public Operand(int value) {
        this.value = value;
        validateNegative(this.value);
    }

    private void validateNegative(int value) {
        if (value < 0) {
            throw new RuntimeException("덧셈 값은 음수일 수 없습니다.");
        }
    }

    public static Operand from(String value) {
        try {
            return convertToOperand(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("정수 외 값은 덧셈 값이 될 수 없습니다.");
        }
    }

    private static Operand convertToOperand(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            return ZERO;
        }

        return new Operand(Integer.parseInt(value));
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
        Operand operand = (Operand) o;
        return value == operand.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
