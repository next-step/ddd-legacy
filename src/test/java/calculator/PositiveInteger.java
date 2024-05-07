package calculator;

public class PositiveInteger {

    private int number;

    public PositiveInteger(String strNumber) {
        this(parseInt(strNumber));
    }

    public PositiveInteger(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("양수만 허용됩니다");
        }
        this.number = number;
    }

    private static int parseInt(String strNumber) {
        try {
            return Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("정수만 허용됩니다");
        }
    }

    public int value() {
        return number;
    }
}
