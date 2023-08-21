package calculator;

public class PositiveNumber {

    private static final String NOT_POSITIVE_MESSAGE = "양수외에 값은 입력불가합니다.";
    private static final int ZERO = 0;

    private int number;

    public PositiveNumber(String number) {
        this.number = toPositive(number);
    }

    private int toPositive(String number) {
        int value = toInt(number);
        if (isNegative(value)) {
            throw new RuntimeException(NOT_POSITIVE_MESSAGE);
        }
        return value;
    }

    private int toInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception error) {
            throw new RuntimeException(NOT_POSITIVE_MESSAGE);
        }
    }

    private boolean isNegative(int value) {
        return value < ZERO;
    }

    public int value() {
        return number;
    }
}
