package stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        Numbers parsedNumbersFromText = NumberParser.parse(text);
        return parsedNumbersFromText.sum();
    }
}
