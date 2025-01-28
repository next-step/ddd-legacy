package calculator;

import exception.InvalidNumberFormatException;
import exception.NotPositiveNumberException;

public class StringValidator {

    private StringValidator() {
        
    }

    public static int validateNumberAndPositive(final String element) {
        if (!element.matches("-?\\d+")) {
            throw new InvalidNumberFormatException();
        }

        int intValue = Integer.parseInt(element);
        if (intValue < 0) {
            throw new NotPositiveNumberException();
        }

        return intValue;
    }
}
