package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class NumberExtractor {

    private static final String ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN = "[^0-9\\s]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(
        String.format("^//%s\n(.*)", ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN)
    );
    private static final String NUMBER_PATTERN = "[1-9][d]*";
    private static final String DEFAULT_DELIMITER = ",|:";

    public static List<String> extract(final String input) {
        if (input.matches(NUMBER_PATTERN)) {
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
            return matcher.group(0).substring(2, 3);
        }

        return DEFAULT_DELIMITER;
    }

    private static String selectExpression(final String input) {
        final var matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (matcher.matches()) {
            return matcher.group(1);
        }

        return input;
    }

    private static void validateExpression(
        final String delimiter,
        final  String expression
    ) {
        final var expressionPattern = String.format(
            "^%s[(%s)%s]*$",
            NUMBER_PATTERN,
            delimiter,
            NUMBER_PATTERN
        );

        if (!expression.matches(expressionPattern)) {
            throw new IllegalArgumentException("입력을 해석할 수 없습니다.");
        }
    }
}
