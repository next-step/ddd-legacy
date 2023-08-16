package calculator;

public class PositiveNumber {
    private final int value;

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("숫자는 음수가 될 수 없습니다");
        }
        this.value = value;
    }

    public static PositiveNumber fromString(String s) {
        try {
            return new PositiveNumber(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닌 문자열이 포함되어 있습니다", e);
        }
    }

    public PositiveNumber plus(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.value + positiveNumber.value);
    }

    public int getValue() {
        return value;
    }
}
