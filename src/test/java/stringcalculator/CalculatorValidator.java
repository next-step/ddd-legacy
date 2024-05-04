package stringcalculator;

import java.util.List;

public class CalculatorValidator {

    public static void negativeNumberValid(final int number) {
        if (number < 0) {
            throw new RuntimeException();
        }
    }
}
