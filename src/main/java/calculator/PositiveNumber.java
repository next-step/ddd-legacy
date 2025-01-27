package calculator;

import calculator.exception.InvalidNumberFormatException;
import calculator.exception.NegativeNumberException;

/**
 * 숫자 검증 및 변환하는 역할
 */
public class PositiveNumber {

    private final int value;

    public PositiveNumber(final String text) {

        if (!isNumeric(text)) {
            throw new InvalidNumberFormatException("Invalid input: Non-numeric value found: " + text);
        }

        int number = Integer.parseInt(text);

        if (number < 0) {
            throw new NegativeNumberException("Negative numbers are not allowed: " + number);
        }
        this.value = number;
    }

    private boolean isNumeric(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getValue() {
        return value;
    }
}
