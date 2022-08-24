package calculator;

import calculator.delimiter.Delimiters;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

public class StringCalculator {
    private static final int ZERO = 0;
    private StringCalculator() {}

    public static int calculate(final String expression, Delimiters delimiters) {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }

        List<String> splitExpressions = delimiters.split(expression);

        splitExpressions.stream()
            .forEach(splitExpression -> validateNumber(splitExpression));

        return splitExpressions.stream()
            .map(splitExpression -> Integer.parseInt(splitExpression))
            .reduce(0, Integer::sum);
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
