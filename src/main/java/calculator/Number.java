package calculator;

import java.util.Objects;

public class Number {

    private final String number;

    public Number(String number) {
        validation(number);
        this.number = number;
    }

    public int getNumber() {
        return Integer.parseInt(number);
    }

    private void validation(String input) {
        if (Integer.parseInt(input) < 0) throw new IllegalArgumentException("입력값은 음수 일 수 없습니다.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number1 = (Number) o;
        return Objects.equals(number, number1.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
