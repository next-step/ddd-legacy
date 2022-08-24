package calculator;

public class PositiveNumber {
    private static final String VALIDATE_EXCEPTION_MESSAGE = "양수만 가능합니다. input: %d";

    private final int value;

    public PositiveNumber(String value) {
        this(Integer.parseInt(value));
    }

    public PositiveNumber(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value <= 0) {
            throw new RuntimeException(String.format(VALIDATE_EXCEPTION_MESSAGE, value));
        }
    }

    public int getValue() {
        return value;
    }
}
