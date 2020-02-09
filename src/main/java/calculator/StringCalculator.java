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

        return Arrays.stream(stringNumbers).map(Integer::parseInt).filter(a -> {
            if (a < 0) {
                throw new RuntimeException();
            }
            return true;
        }).reduce(0, Integer::sum);
    }
}
