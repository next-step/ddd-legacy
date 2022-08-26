package stringcalculator;

import java.util.stream.Stream;

import static stringcalculator.StringParser.parseNumbers;

public class StringCalculator {

    private static final int ZERO = 0;

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return ZERO;
        }
        return Stream.of(parseNumbers(text))
                .map(PositiveNumber::of)
                .reduce(PositiveNumber.zero(), PositiveNumber::add)
                .getNumber();
    }
}
