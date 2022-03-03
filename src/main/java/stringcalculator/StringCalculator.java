package stringcalculator;

import java.util.List;

public class StringCalculator {

    private final NumberParser numberParser;

    public StringCalculator(NumberParser numberParser) {
        this.numberParser = numberParser;
    }

    public PositiveNumber add(String text) {
        final List<Integer> numbers = numberParser.parse(text);

        return numbers.stream()
            .map(PositiveNumber::new)
            .reduce(PositiveNumber.ZERO, PositiveNumber::plus);
    }
}
