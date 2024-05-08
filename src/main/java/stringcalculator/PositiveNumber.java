package stringcalculator;

import java.util.Objects;

public class PositiveNumber {
    public static final String DIGIT_NUMBER = "\\d+";
    private final int number;

    public PositiveNumber(String value) {
        if (!validate(value)) {
            this.number = 0;
            return;
        }
        this.number = Integer.parseInt(value);
    }

    public static PositiveNumber of(String value) {
        return new PositiveNumber(value);
    }

    private boolean validate(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (!value.matches(DIGIT_NUMBER)) {
            throw new IllegalArgumentException("올바른 숫자값을 입력하세요.");
        }
        if (Integer.parseInt(value) < 0) {
            throw new IllegalArgumentException("음수는 입력 불가");
        }
        return true;
    }

    public int number() {
        return number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PositiveNumber)) {
            return false;
        }
        return ((PositiveNumber) obj).number() == this.number;
    }
}
