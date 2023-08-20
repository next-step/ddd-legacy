package calculator;

public class PositiveNumber {

    private static final String NOT_NEGATIVE_MESSAGE = "음수는 입력불가합니다.";
    private static final int ZERO = 0;

    private int number;

    public PositiveNumber(String number) {
        this.number = toPositive(number);
    }

    private int toPositive(String number) {
        int value = Integer.parseInt(number);
        if (isNegative(value)) {
            throw new RuntimeException(NOT_NEGATIVE_MESSAGE);
        }
        return value;
    }

    private boolean isNegative(int value) {
        return value < ZERO;
    }

    public int value() {
        return number;
    }
}
