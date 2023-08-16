package calculator;

public class Number {
    private final int value;

    public Number(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("숫자는 음수가 될 수 없습니다");
        }
        this.value = value;
    }

    public static Number fromString(String s) {
        try {
            return new Number(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닌 문자열이 포함되어 있습니다", e);
        }
    }

    public Number plus(Number number) {
        return new Number(this.value + number.value);
    }

    public int getValue() {
        return value;
    }
}
