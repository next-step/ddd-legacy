package calculator;

public class Number {
    private static final int ZERO = 0;
    static final String PARSING_INTEGER_EXCEPTION = "숫자로 변환할 수 없는 문자열 입니다.";
    static final String NEGATIVE_NUMBER_EXCEPTION = "음수는 유효하지 않은 숫자입니다.";
    private final int value;
    public Number(final String value) {
        this(parseInt(value));
    }

    public Number(final int value) {
        validateNumber(value);
        this.value = value;
    }

    private static void validateNumber(final int value) {
        if (value < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_EXCEPTION);
        }
    }

    private static int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(PARSING_INTEGER_EXCEPTION);
        }
    }
}
