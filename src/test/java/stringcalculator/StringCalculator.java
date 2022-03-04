package stringcalculator;

public class StringCalculator {
    private static final String DELIMITER = ",|:";

    public int add(String text) {
        final PositiveNumbers positiveNumbers = TextConverter.convertToNumbers(text, DELIMITER);
        return positiveNumbers.sum().getValue();
    }
}
