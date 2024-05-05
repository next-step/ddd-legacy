package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_SEPARATOR = "[,:]";
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int MATCHER_BODY_INDEX = 2;
    private final CustomStategy CUSTOM_STRATEGY = new CustomStategy();

    public static int getSum2(final String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return splitAndGetSum(input, DEFAULT_SEPARATOR);
        }
        String customSeparator = matcher.group(CUSTOM_SEPARATOR_INDEX);
        return splitAndGetSum(matcher.group(MATCHER_BODY_INDEX), customSeparator);
    }

    public int getSum(final String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        if (!CUSTOM_STRATEGY.isCustom(input)) {
            return splitAndGetSum2(input, new DefaultStrategy());
        }

        return splitAndGetSum2(CUSTOM_STRATEGY.getBody(), CUSTOM_STRATEGY);
    }

    private static int splitAndGetSum(String matcher, final String separator) {
        return Arrays.stream(matcher.trim().split(separator))
                .map(CalculatorNumber::from)
                .reduce(CalculatorNumber.from("0"), CalculatorNumber::plus)
                .getNumber();
    }

    private static int splitAndGetSum2(String matcher, final CalculatorStrategy calculator) {
        return Arrays.stream(matcher.trim().split(calculator.getSeparator(matcher)))
                .map(CalculatorNumber::from)
                .reduce(CalculatorNumber.from("0"), CalculatorNumber::plus)
                .getNumber();
    }

}
