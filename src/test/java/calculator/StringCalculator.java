package calculator;

import calculator.utils.StringUtils;
import java.util.List;

public final class StringCalculator {

    private static final int DEFAULT_VALUE = 0;

    private final StringNumberParser stringNumberParser;

    public StringCalculator() {
        stringNumberParser = new StringNumberParser();
    }

    public int add(String text) {
        if (StringUtils.isBlank(text)) {
            return DEFAULT_VALUE;
        }

        return sum(toPositiveNumbers(text));
    }

    private List<PositiveNumber> toPositiveNumbers(String text) {
        return stringNumberParser.toPositiveNumbers(text);
    }

    private int sum(List<PositiveNumber> positiveNumbers) {
        return positiveNumbers.stream()
                .reduce(PositiveNumber::sum)
                .map(PositiveNumber::getValue)
                .orElse(DEFAULT_VALUE);
    }
}
