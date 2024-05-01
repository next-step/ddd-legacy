package calculator;

import calculator.exception.NegativeNumberException;

public class Number {

    public static final int ZERO_NUMBER = 0;
    private final int number;

    public Number(String token) {
        this(Integer.parseInt(token));
    }

    public Number(int number) {
        validateNumber(number);
        this.number = number;
    }

    private void validateNumber(int number) {
        if (isNegative(number)) {
            throw new NegativeNumberException(number + "is Negative");
        }
    }

    private boolean isNegative(int number) {
        return number < 0;
    }

    public int getNumber() {
        return number;
    }
}
