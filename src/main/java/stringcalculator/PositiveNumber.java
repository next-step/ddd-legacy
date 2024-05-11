package stringcalculator;

public class PositiveNumber {

    private static final String NEGATIVE_INTEGER_NOT_ALLOWED = "음수는 입력할 수 없습니다 (입력값: %s)";
    private static final String INVALID_INPUT_MESSAGE = "숫자를 입력해주세요 (입력값: %s)";

    public static final PositiveNumber ZERO = new PositiveNumber(0);

    private final int number;

    public PositiveNumber(final int number) {
        checkPositive(number);
        this.number = number;
    }

    public static PositiveNumber byString(String number) {
        checkPositive(parseInt(number));
        return new PositiveNumber(Integer.parseInt(number));
    }

    private static int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(INVALID_INPUT_MESSAGE, number));
        }
    }

    public PositiveNumber add(PositiveNumber other) {
        return new PositiveNumber(number + other.number);
    }

    private static void checkPositive(int number) {
        if (number < 0) {
            throw new IllegalArgumentException(String.format(NEGATIVE_INTEGER_NOT_ALLOWED, number));
        }
    }

    public int value() {
        return number;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositiveNumber that)) return false;

        return number == that.number;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
