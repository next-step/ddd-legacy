package stringcalculator;

public class PositiveNumber {

    private static final String POSITIVE_INTEGER_REGEX = "^\\d+$";
    private static final String NEGATIVE_INTEGER_NOT_ALLOWED = "음수는 입력할 수 없습니다 %s";

    private final int number;

    private PositiveNumber(final int number) {
        this.number = number;
    }

    public static PositiveNumber byString(String number) {
        checkPositive(number);
        return new PositiveNumber(Integer.parseInt(number));
    }

    private static void checkPositive(String number) {
        if (!number.matches(POSITIVE_INTEGER_REGEX)) {
            throw new IllegalArgumentException(String.format(NEGATIVE_INTEGER_NOT_ALLOWED, number));
        }
    }

    public int value() {
        return number;
    }
}
