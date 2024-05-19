package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class NumberExtractor {

    private static final String ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN = "[^0-9\\s]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(
        String.format("^//%s\n(.*)", ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN)
    );
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[1-9][d]*");
    private static final String EXPRESSION_REGEX_FORMAT = "^%s[(%s)%s]*$";
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final int CUSTOM_DELIMITER_GROUP = 0;
    private static final int CUSTOM_DELIMITER_START_OFFSET = 2;
    private static final int CUSTOM_DELIMITER_END_OFFSET = 3;
    private static final int EXPRESSION_GROUP = 1;

    public static List<String> extract(final String input) {
        if (NUMBER_PATTERN.matcher(input).matches()) {
            return List.of(input);
        }

        final String delimiter = selectDelimiter(input);
        final String expression = selectExpression(input);

        validateExpression(delimiter, expression);

        return Arrays.asList(expression.split(delimiter));
    }

    private static String selectDelimiter(final String input) {
        final var matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matcher.matches()) {
            return matcher.group(CUSTOM_DELIMITER_GROUP)
                .substring(CUSTOM_DELIMITER_START_OFFSET, CUSTOM_DELIMITER_END_OFFSET);
        }

        return DEFAULT_DELIMITER;
    }

    private static String selectExpression(final String input) {
        final var matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matcher.matches()) {
            return matcher.group(EXPRESSION_GROUP);
        }

        return input;
    }

    private static void validateExpression(
        final String delimiter,
        final  String expression
    ) {
        final var expressionPattern = String.format(
            EXPRESSION_REGEX_FORMAT,
            NUMBER_PATTERN,
            delimiter,
            NUMBER_PATTERN
        );

        if (!expression.matches(expressionPattern)) {
            throw new IllegalArgumentException("입력을 해석할 수 없습니다.");
        }
    }
}
