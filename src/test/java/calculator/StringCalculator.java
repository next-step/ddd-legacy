package calculator;

import calculator.utils.StringUtils;
import java.util.List;

public final class StringCalculator {

    private final StringNumberParser stringNumberParser;

    public StringCalculator() {
        stringNumberParser = new StringNumberParser();
    }

    public int add(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }

        return sum(toPositiveNumbers(text));
    }

    private List<PositiveNumber> toPositiveNumbers(String text) {
        return stringNumberParser.toPositiveNumbers(text);
    }

    private int sum(List<PositiveNumber> positiveNumbers) {
        return positiveNumbers.stream()
                .mapToInt(PositiveNumber::getValue)
                .sum();
    }
}
