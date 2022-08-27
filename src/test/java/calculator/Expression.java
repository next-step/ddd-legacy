package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Expression {

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int EXPRESSION_GROUP_INDEX = 2;
    private static final int DELIMITER_GROUP_INDEX = 1;

    private final String expression;
    private final String delimiter;

    private Expression(final String expression, final String delimiter) {
        this.expression = Objects.requireNonNull(expression);
        this.delimiter = Objects.requireNonNull(delimiter);
    }

    static Expression of(final String input) {
        validateNonNull(input);

        final Matcher customDelimiterExpression = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (!customDelimiterExpression.find()) {
            return new Expression(input, DEFAULT_DELIMITER);
        }

        return new Expression(
            customDelimiterExpression.group(EXPRESSION_GROUP_INDEX),
            customDelimiterExpression.group(DELIMITER_GROUP_INDEX)
        );
    }

    private static void validateNonNull(String input) {
        if (input == null) {
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
