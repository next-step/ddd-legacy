package calculator;

import common.numeric.NonNegativeNumber;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class StringExpression {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int EXPRESSION_GROUP_INDEX = 2;
    private static final int DELIMITER_GROUP_INDEX = 1;

    private final String expression;
    private final String delimiter;

    private StringExpression(final String expression, final String delimiter) {
        this.expression = Objects.requireNonNull(expression);
        this.delimiter = Objects.requireNonNull(delimiter);
    }

    static StringExpression of(final String input) {
        validateNonEmpty(input);

        final Matcher customDelimiterExpression = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (!customDelimiterExpression.find()) {
            return new StringExpression(input, DEFAULT_DELIMITER);
        }

        return new StringExpression(
            customDelimiterExpression.group(EXPRESSION_GROUP_INDEX),
            customDelimiterExpression.group(DELIMITER_GROUP_INDEX)
        );
    }

    private static void validateNonEmpty(String input) {
        if (input == null || input.trim().length() == 0) {
            throw new RuntimeException();
        }
    }

    List<Integer> parse() {
        return Arrays.stream(this.expression.split(this.delimiter))
            .map(NonNegativeNumber::new)
            .map(NonNegativeNumber::getInt)
            .collect(Collectors.toUnmodifiableList());
    }
}
