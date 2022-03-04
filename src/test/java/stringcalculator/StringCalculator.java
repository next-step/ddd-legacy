package stringcalculator;

import java.util.Arrays;

import static stringcalculator.StringUtils.isBlank;
import static stringcalculator.StringUtils.parseTokens;

public class StringCalculator {

    private static final int BLANK_NUMBER = 0;

    public int add(final String text) {
        if (isBlank(text)) {
            return BLANK_NUMBER;
        }

        return Arrays.stream(parseTokens(text))
                .mapToInt(StringUtils::parsePositiveInteger)
                .sum();
    }

}
