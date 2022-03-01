package stringcalculator;

public class PositiveNumber {
    private static final String NEGATIVE_NUMBER_NOT_ALLOWED = "음수는 허용되지 않습니다. 입력된 수: ";

    private final int value;

    public PositiveNumber() {
        this(0);
    }

    public PositiveNumber(int value) {
        if (value < 0) {
            throw new RuntimeException(NEGATIVE_NUMBER_NOT_ALLOWED + value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
