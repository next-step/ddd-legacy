package calculator;

import java.util.Objects;
import java.util.regex.Pattern;

public class Number {
    private static final Pattern NUMBER_REGEX = Pattern.compile("^[0-9]*$");

    private final String number;

    public Number(String number) {
        validation(number);
        this.number = number;
    }

    public int getNumber() {
        return Integer.parseInt(number);
    }

    private void validation(String input) {
        if (!NUMBER_REGEX.matcher(input).find()) throw new IllegalArgumentException("입력값은 숫자 여야 합니다");
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
