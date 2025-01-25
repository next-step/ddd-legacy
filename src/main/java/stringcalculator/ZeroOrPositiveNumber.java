package stringcalculator;

import java.util.Objects;

public class ZeroOrPositiveNumber {

    private final int number;

    public ZeroOrPositiveNumber(String number) {
        this(toInt(number));
    }

    private static int toInt(String number) {
        int numberInt;
        try {
            numberInt = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("문자형 숫자만 입력 가능합니다");
        }
        return numberInt;
    }

    public ZeroOrPositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("음수는 계산할 수 없습니다.");
        }
        this.number = number;
    }

    public ZeroOrPositiveNumber sum(ZeroOrPositiveNumber otherNumber) {
        return new ZeroOrPositiveNumber(number + otherNumber.getNumber());
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZeroOrPositiveNumber that = (ZeroOrPositiveNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
