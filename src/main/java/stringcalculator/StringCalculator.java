package stringcalculator;

public class StringCalculator {

    public int add(NumericString numericString) {
        if (numericString.isEmpty()) {
            return NumericString.ZERO;
        }

        return numericString.sum();
    }
}
