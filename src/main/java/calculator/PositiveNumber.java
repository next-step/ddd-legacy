package calculator;

import exception.InvalidNumberFormatException;
import exception.NotPositiveNumberException;

public class PositiveNumber {

    private final int value;

    public PositiveNumber(final String element) {
        if (!element.matches("-?\\d+")) {
            throw new InvalidNumberFormatException();
        }

        int intValue = Integer.parseInt(element);
        if (intValue < 0) {
            throw new NotPositiveNumberException();
        }

        this.value = intValue;
    }

    public int getValue() {
        return this.value;
    }
}
