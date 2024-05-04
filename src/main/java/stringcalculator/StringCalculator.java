package stringcalculator;

public class StringCalculator {

    public int add(NumericString numericString) {
        if (numericString.isEmpty()) {
            return NumericString.ZERO;
        }

        PositiveNumbers numbers = numericString.toNumbers();

        return numbers.sum();
    }
}
