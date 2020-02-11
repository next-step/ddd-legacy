package calculator;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private static final int ZERO = 0;
    private Parser parser;

    public StringCalculator(Parser parser) {
        this.parser = parser;
    }

    int add(final String text) {
        if (StringUtils.isBlank(text)) {
            return ZERO;
        }

        String[] stringNumbers = parser.parseStrings(text);

        return Arrays.stream(stringNumbers).map(PositiveNumber::of).map(PositiveNumber::toInt).reduce(ZERO, Integer::sum);
    }
}
