package stringcalculator;

import java.util.Objects;

public record Number(
        int number
) {
    public static Number of(String number) {
        if (isNegativeNumber(number) || isNotNumber(number)) {
            throw new RuntimeException("0 혹은 양수만 입력 가능합니다.");
        }
        return new Number(Integer.parseInt(number));
    }

    private static boolean isNegativeNumber(String s) {
        return Integer.parseInt(s) < 0;
    }

    private static boolean isNotNumber(String s) {
        return !s.chars().allMatch(Character::isDigit);
    }

    public int number() {
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
