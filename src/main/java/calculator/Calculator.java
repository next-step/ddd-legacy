package calculator;

import java.util.List;

public class Calculator {
    private static final int DEFAULT_SUM = 0;
    private static final String BLANK_TEXT_REGEX = "[\"']+";

    private final TextParser<PositiveNumber> textParser;

    public Calculator(TextParser<PositiveNumber> textParser) {
        this.textParser = textParser;
    }

    public int calculate(String text) {
        if (text == null || text.isEmpty()) {
            return DEFAULT_SUM;
        }

        if (text.matches(BLANK_TEXT_REGEX)) {
            return DEFAULT_SUM;
        }

        List<PositiveNumber> parseText = textParser.parse(text);
        return calculateSum(parseText);
    }

    private int calculateSum(List<PositiveNumber> values) {
        return values.stream().mapToInt(PositiveNumber::getPrimitiveValue).sum();
    }
}
