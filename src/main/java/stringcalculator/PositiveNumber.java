package stringcalculator;

import java.util.Objects;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수 값은 입력할 수 없습니다.");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static PositiveNumber parseToken(String token) {
        try {
            int number = Integer.parseInt(token);
            return new PositiveNumber(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자 이외의 값이 입력되었습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositiveNumber that = (PositiveNumber) o;

        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
