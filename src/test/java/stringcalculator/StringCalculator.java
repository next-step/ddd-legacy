package stringcalculator;

import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private final CustomStrategy CUSTOM_STRATEGY = new CustomStrategy();


    public int getSum(final String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        if (!CUSTOM_STRATEGY.isCustom(input)) {
            return splitAndGetSum(input, new DefaultStrategy());
        }

        return splitAndGetSum(CUSTOM_STRATEGY.getBody(), CUSTOM_STRATEGY);
    }

    private static int splitAndGetSum(String matcher, final CalculatorStrategy calculator) {
        return Arrays.stream(matcher.trim().split(calculator.getSeparator(matcher)))
                .map(CalculatorNumber::from)
                .reduce(CalculatorNumber.from("0"), CalculatorNumber::plus)
                .getNumber();
    }

}
