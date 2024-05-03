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

    public static int getSum(final String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return splitAndGetSum2(input, GENERAL_SEPARATOR);
        }

        String customSeparator = matcher.group(CUSTOM_SEPARATOR_INDEX);
        return splitAndGetSum2(matcher.group(MATCHER_BODY_INDEX), customSeparator);
    }

    private static int splitAndGetSum(String matcher, String separator) {
        return CalculatorNumber.sum(Arrays.stream(matcher.trim().split(separator))
                .map(CalculatorNumber::from));
    }

    private static int splitAndGetSum2(String matcher, String separator) {
        String[] strings = matcher.trim().split(separator);
        CalculatorNumber result = null;
        for (String string : strings) {
            CalculatorNumber number = CalculatorNumber.from(string);
            if (result == null) {
                result = number;
            } else {
                result = result.plus(number);
            }
        }
        return (result != null) ? result.getNumber() : 0;
    }

}
