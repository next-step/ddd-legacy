package calculator.domain;

import java.util.List;

public class StringAdderCalculator {

    private final PositiveNumberExtractor positiveNumberExtractor;

    public StringAdderCalculator(PositiveNumberExtractor positiveNumberExtractor) {
        this.positiveNumberExtractor = positiveNumberExtractor;
    }

    public PositiveStringNumber calculate(String expression) {
        List<PositiveStringNumber> positiveStringNumbers = positiveNumberExtractor.extractNumbers(expression);

        return positiveStringNumbers.stream()
            .reduce(PositiveStringNumber::add)
            .orElse(PositiveStringNumber.ZERO);
    }
}
