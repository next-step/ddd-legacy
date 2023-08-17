package calculator;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int MIN_SIZE = 1;
    private static final int MIN_RESULT = 0;

    public int add(String argument) {
        int result = 0;

        if (StringUtils.isEmpty(argument)) {
            return MIN_RESULT;
        }

        if (argument.length() == MIN_SIZE) {
            return Integer.parseInt(argument);
        }

        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(argument);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(customDelimiter);
            for (String token : tokens) {
                result += Integer.parseInt(token);
            }
        }

        String[] operands = argument.split("[,:]");
        for (String operand : operands) {
            result += Integer.parseInt(operand);
        }

        return result;
    }
}
