package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int EXPRESSION_GROUP_INDEX = 2;
    private static final int DELIMITER_GROUP_INDEX = 1;

    public int add(final String expression) {
        if (isBlank(expression)) {
            return 0;
        }

        final Matcher customDelimiterExpression = CUSTOM_DELIMITER_PATTERN.matcher(expression);
        if (!customDelimiterExpression.find()) {
            return sum(expression, DEFAULT_DELIMITER);
        }

        return sum(
            customDelimiterExpression.group(EXPRESSION_GROUP_INDEX),
            customDelimiterExpression.group(DELIMITER_GROUP_INDEX)
        );
    }

    private boolean isBlank(String expression) {
        return expression == null || expression.trim().length() == 0;
    }

    private int sum(final String expression, final String delimiter) {
        return Arrays.stream(expression.split(delimiter))
            .mapToInt(this::parseNonNegativeInt)
            .sum();
    }

    private int parseNonNegativeInt(final String strNumber) {
        final int number = parseInt(strNumber);
        if (number < 0) {
            throw new RuntimeException();
        }
        return number;
    }

    private int parseInt(final String strNumber) {
        try {
            return Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }
}
