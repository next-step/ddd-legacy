package stringcalculator;

public class StringCalculator {

    private final PositiveNumberParser positiveNumberParser;

    public StringCalculator(PositiveNumberParser positiveNumberParser) {
        this.positiveNumberParser = positiveNumberParser;
    }

    public PositiveNumber add(String text) {
        return positiveNumberParser.parse(text)
            .sum();
    }
}
