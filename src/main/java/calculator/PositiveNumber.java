package calculator;

public class PositiveNumber {
    private int number;

    private PositiveNumber(int number) {
        this.number = number;
    }

    public static PositiveNumber of(String number) throws RuntimeException {
        try {
            return PositiveNumber.of(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    public static PositiveNumber of(int number) throws RuntimeException {
        if (isNegativeNumber(number)) {
            throw new RuntimeException();
        }
        return new PositiveNumber(number);
    }

    private static boolean isNegativeNumber(int number) {
        return number < 0;
    }

    public int getNumber() {
        return number;
    }

}
