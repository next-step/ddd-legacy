package calculator;

public class Number {
    private static final String NOT_NUMBER_EXCEPTION = "숫자 외의 값을 넣을 수 없습니다.";

    private final int value;

    public Number(String input) {
        this(parseToInt(input));
    }

    public Number(int value) {
        this.value = value;
    }

    private static int parseToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(NOT_NUMBER_EXCEPTION);
        }
    }

    public boolean isNegative() {
        return value < 0;
    }

    public int getValue() {
        return value;
    }
}
