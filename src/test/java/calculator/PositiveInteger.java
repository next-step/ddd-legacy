package calculator;

import static java.lang.Integer.parseInt;

public class PositiveInteger {
    private final int value;

    public PositiveInteger(int value) {
        if (value < 0) {
            throw new RuntimeException("음수는 계산할 수 없습니다.");
        }
        this.value = value;
    }

    public static PositiveInteger parse(String value) {
        return new PositiveInteger(tryParseInt(value));
    }

    private static int tryParseInt(String value) {
        try {
            return parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력해주세요.");
        }
    }

    public int getValue() {
        return value;
    }
}
