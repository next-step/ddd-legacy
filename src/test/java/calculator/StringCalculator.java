package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class StringCalculator {
    private static final int ZERO = 0;
    private static final List<String> SEPARATORS = Arrays.asList(",", ":");
    private static final String CUSTOM_SEPARATORS = "//(.)\n(.*)";
    private StringCalculator() {}

    public static int calculate(final String expression) {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }

        List<String> splitExpressions = Arrays.asList(expression);
        for (String delimiter : SEPARATORS) {
            splitExpressions = split(splitExpressions, delimiter);
        }

        for (String splitExpression : splitExpressions) {
            Matcher matcher = Pattern.compile(CUSTOM_SEPARATORS).matcher(splitExpression);
            if (matcher.find()) {
                String delimiter = matcher.group(1);
                splitExpressions = Arrays.asList(matcher.group(2).split(delimiter));
            }
        }

        splitExpressions.stream()
            .forEach(splitExpression -> validateNumber(splitExpression));

        return splitExpressions.stream()
            .map(splitExpression -> Integer.parseInt(splitExpression))
            .reduce(0, Integer::sum);
    }

    private static List<String> split(final List<String> expressions, String delimeter) {
        List<String> result = new ArrayList<>();
        expressions.stream()
            .forEach(expression -> result.addAll(Arrays.asList(expression.split(delimeter))));

        return result;
    }

    private static void validateNumber(String number) {
        if (number.chars()
              .noneMatch(c -> Character.isDigit(c))) {
            throw new RuntimeException("숫자가 아닌 문자는 입력 불가능 합니다.");
        }

        if (Integer.parseInt(number) < ZERO) {
            throw new RuntimeException("0보다 작은 숫자는 입력 불가능합니다.");
        }
    }
}
