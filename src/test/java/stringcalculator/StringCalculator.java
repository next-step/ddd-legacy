package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static stringcalculator.StringUtils.isBlank;

public class StringCalculator {

    private static final int BLANK_NUMBER = 0;
    private static final String DEFAULT_SEPARATOR = "[,:]";

    private static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(final String text) {
        if (isBlank(text)) {
            return BLANK_NUMBER;
        }

        Matcher customPatternMatcher = CUSTOM_SEPARATOR_PATTERN.matcher(text);
        if (customPatternMatcher.find()) {
            return calculate(
                    customPatternMatcher.group(2),
                    customPatternMatcher.group(1)
            );
        }

        return calculate(text);
    }

    private int calculate(String text) {
        return this.calculate(text, DEFAULT_SEPARATOR);
    }

    private int calculate(String text, String separator) {
        return Arrays.stream(text.split(separator))
                .mapToInt(StringUtils::parsePositiveInteger)
                .sum();
    }

}
