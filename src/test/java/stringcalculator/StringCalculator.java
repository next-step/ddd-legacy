package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private final CustomStrategy CUSTOM_STRATEGY = new CustomStrategy();
    private final DefaultStrategy DEFAULT_STRATEGY = new DefaultStrategy();

    public int getSum(final String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        CalculatorStrategy strategy = DEFAULT_STRATEGY;
        String body = input;
        if (CUSTOM_STRATEGY.isCustom(input)) {
            strategy = CUSTOM_STRATEGY;
            body = CUSTOM_STRATEGY.getBody();
        }

        String separator = strategy.getSeparator();
        return splitAndGetSum(body, separator);
    }

    private int splitAndGetSum(String matcher, String separator) {
        return Arrays.stream(matcher.trim().split(separator))
                .map(CalculatorNumber::from)
                .reduce(CalculatorNumber.from("0"), CalculatorNumber::plus)
                .getNumber();
    }

}
