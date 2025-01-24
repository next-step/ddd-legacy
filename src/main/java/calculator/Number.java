package calculator;

public class Number {
    private final int value;

    public Number(String input) {
        this.value = parseAndValidate(input);
    }

    public int value() {
        return value;
    }

    private int parseAndValidate(String input) {
        int parsed = parseNumber(input);
        validateNonNegative(parsed);
        return parsed;
    }

    private int parseNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다: " + input);
        }
    }

    private void validateNonNegative(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다: " + num);
        }
    }
}