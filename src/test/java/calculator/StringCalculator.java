package calculator;

import static calculator.DelimiterPattern.CUSTOM_REGEX;
import static calculator.DelimiterPattern.DEFAULT_REGEX;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String input) {
        int sum = 0;
        if (StringUtils.isEmpty(input)) {
            return sum;
        }
        Matcher matcher = getCustomPatternMatcher(input);
        if (matcher.find()) {
            return getSum(sum, getTokenByCustomDelimiter(matcher));
        }
        return getSum(sum, getTokenByDefaultDelimiter(input));
    }

    private Matcher getCustomPatternMatcher(String input) {
        return Pattern.compile(CUSTOM_REGEX).matcher(input);
    }

    private String[] getTokenByCustomDelimiter(Matcher m) {
        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }
    private String[] getTokenByDefaultDelimiter(String input) {
        return input.split(DEFAULT_REGEX);
    }

    private int getSum(int sum, String[] tokens) {
        for (String number : tokens) {
            if (!isNaturalNumber(number)) {
                throw new RuntimeException();
            }
            sum += Integer.parseInt(number);
        }
        return sum;
    }

    private boolean isNaturalNumber(String param) {
        char[] chars = param.toCharArray();
        for (char aChar : chars) {
            if (aChar > '9' || aChar < '0') {
                return false;
            }
        }
        return true;
    }

}
