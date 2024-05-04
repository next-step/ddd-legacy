package stringcalculator;

public class PositiveNumber {

    private static final String POSITIVE_INTEGER_REGEX = "^\\d+$";

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
            throw new IllegalArgumentException(String.format("Negative numbers are not allowed %s", number));
        }
    }

    public int value() {
        return number;
    }
}
