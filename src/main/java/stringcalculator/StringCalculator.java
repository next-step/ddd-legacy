package stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        DelimitedNumbers delimitedNumbers = new DelimitedNumbers(text);
        return delimitedNumbers.getNumbersStream()
                .mapToInt(number -> new PositiveNumber(number).getValue())
                .sum();
    }

}