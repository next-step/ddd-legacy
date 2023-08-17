package calculator.domain;

public class PositiveStringNumber {

    public static final int POSITIVE_BOUNDARY = 0;
    private final String value;

    public PositiveStringNumber(String value) {
        validatePositiveStringNumber(value);
        this.value = value;
    }

    private void validatePositiveStringNumber(String value) {
        try {
            if (Integer.parseInt(value) < POSITIVE_BOUNDARY) {
                throw new RuntimeException("덧셈에는 양수만 허용됩니다. value = " + value);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("덧셈에는 숫자만 허용됩니다. value = " + value);
        }
    }
}
