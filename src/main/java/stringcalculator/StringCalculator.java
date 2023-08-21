package stringcalculator;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        StringParser stringParser = new StringParser(text);
        return stringParser.getNumbersStream()
                .mapToInt(number -> new PositiveNumber(number).getValue())
                .sum();
    }

}