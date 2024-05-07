package calculator;

public class Token {

    private final int positiveInteger;

    public Token(String strNumber) {
        this(parseInt(strNumber));
    }

    public Token(int positiveInteger) {
        if (positiveInteger < 0) {
            throw new IllegalArgumentException("양수만 허용됩니다");
        }
        this.positiveInteger = positiveInteger;
    }

    private static int parseInt(String strNumber) {
        try {
            return Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("정수만 허용됩니다");
        }
    }

    public int value() {
        return positiveInteger;
    }
}
