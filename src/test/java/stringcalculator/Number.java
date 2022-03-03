package stringcalculator;

public class Number {

    public static Number ZERO = new Number(0);

    private final int number;

    public Number(String value) {
        this.number = changeNumber(value);
    }

    private Number(int number) {
        this.number = number;
    }

    private int changeNumber(String value) {
        if (Integer.parseInt(value) < 0) {
            throw new IllegalArgumentException("0 보다 작은 값은 더할 수 없습니다.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("숫자만 입력 가능합니다.");
        }
    }

    public Number plus(Number other) {
        return new Number(number + other.number);
    }

    public int getValue() {
        return number;
    }
}
