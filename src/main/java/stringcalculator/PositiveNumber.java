package stringcalculator;

public class PositiveNumber {

    public static final int MIN_NUMBER = 0;

    private final int number;

    public PositiveNumber(String token) {
        int parsedNumber = Integer.parseInt(token);
        validateNegative(parsedNumber);
        this.number = parsedNumber;
    }

    private static void validateNegative(int number) {
        if (number < MIN_NUMBER) {
            throw new RuntimeException();
        }
    }

    public int getNumber() {
        return number;
    }
}
