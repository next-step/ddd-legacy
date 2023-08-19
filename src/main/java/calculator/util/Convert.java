package calculator.util;

import calculator.exception.NegativeInputException;

public final class Convert {

    public static int positiveNumber(final String input) {
        final int number = Integer.parseInt(input);
        if (number < 0) {
            throw new NegativeInputException();
        }
        return number;
    }

}
