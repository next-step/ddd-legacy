package stringcalculator;

public class StringCalculator {

    public PositiveNumber add(NumericString numericString) {
        if (numericString.isEmpty()) {
            return PositiveNumber.ZERO;
        }

        return numericString.sum();
    }
}
