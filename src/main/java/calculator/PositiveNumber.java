package calculator;

public class PositiveNumber {
    private int number;

    private PositiveNumber(int number) {
        this.number = number;
    }

    public static PositiveNumber of(String number) throws RuntimeException {
        return PositiveNumber.of(Integer.parseInt(number));
    }

    public static PositiveNumber of(int number) throws NumberFormatException {
        if (isNegativeNumber(number)) {
            throw new NumberFormatException();
        }
        return new PositiveNumber(number);
    }

    private static boolean isNegativeNumber(int number) {
        return number < 0;
    }

    public int toInt() {
        return number;
    }
}
