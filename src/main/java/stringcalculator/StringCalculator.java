package stringcalculator;

import java.util.List;

public class StringCalculator {

    private final NumberParser numberParser;

    public StringCalculator(NumberParser numberParser) {
        this.numberParser = numberParser;
    }

    public PositiveNumber add(String text) {
        if (isNullOrEmpty(text)) {
            return PositiveNumber.ZERO;
        }

        final List<Integer> numbers = numberParser.parse(text);

        return numbers.stream()
            .map(PositiveNumber::new)
            .reduce(PositiveNumber.ZERO, PositiveNumber::plus);
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
