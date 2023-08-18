package calculator.domain;

public class StringAdderCalculator {

    private final PositiveNumberExtractor positiveNumberExtractor;

    public StringAdderCalculator(PositiveNumberExtractor positiveNumberExtractor) {
        this.positiveNumberExtractor = positiveNumberExtractor;
    }

    public PositiveStringNumber calculate(String expression) {
        PositiveStringNumbers positiveStringNumbers = positiveNumberExtractor.extractNumbers(expression);

        return positiveStringNumbers.addAll();
    }
}
