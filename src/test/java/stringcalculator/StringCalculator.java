package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final String GENERAL_SEPARATOR = "[,:]";
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int MATCHER_BODY_INDEX = 2;

    public static int getSum(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return splitAndGetSum(input, GENERAL_SEPARATOR);
        }

        String customSeparator = matcher.group(CUSTOM_SEPARATOR_INDEX);
        return splitAndGetSum(matcher.group(MATCHER_BODY_INDEX), customSeparator);
    }

    private static int splitAndGetSum(String matcher, String separator) {
        return Arrays.stream(matcher.trim().split(separator))
                .map(CalculatorNumber::new)
                .mapToInt(CalculatorNumber::getNumber)
                .sum();
    }
}
