package calculator;

import calculator.exception.NegativeNumberException;

public class PositiveNumber {

    public static final int ZERO_NUMBER = 0;
    private final int number;

    public PositiveNumber(String token) {
        this(Integer.parseInt(token));
    }

    public PositiveNumber(int number) {
        validateNumber(number);
        this.number = number;
    }

    private void validateNumber(int number) {
        if (!isPositive(number)) {
            throw new NegativeNumberException(number + "is Negative");
        }
    }

    private boolean isPositive(int number) {
        return number >= 0;
    }

    public int getNumber() {
        return number;
    }
}
