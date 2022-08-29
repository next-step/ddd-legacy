package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile(DEFAULT_DELIMITER_REGEX);
    private static final int EMPTY_EXPRESSION_RESULT = 0;

    public int add(String text) {
        if (text == null) {
            return EMPTY_EXPRESSION_RESULT;
        }

        final var expression = new AdditionExpression(text);

        final var delimiterPattern = compileDelimiterPattern(expression.getCustomDelimiter());

        if (expression.isTokensBlank()) {
            return EMPTY_EXPRESSION_RESULT;
        }

        final String[] tokens = splitTokensByDelimiter(expression.getTokens(), delimiterPattern);

        return Arrays.stream(tokens)
                .map(PositiveInteger::parse)
                .mapToInt(PositiveInteger::getValue)
                .sum();
    }

    private String[] splitTokensByDelimiter(String tokensPart, Pattern delimiterRegex) {
        return delimiterRegex.split(tokensPart);
    }

    private Pattern compileDelimiterPattern(String customizedDelimiter) {
        if (customizedDelimiter == null) {
            return DEFAULT_DELIMITER_PATTERN;
        }

        return Pattern.compile(DEFAULT_DELIMITER_REGEX + "|" + customizedDelimiter);
    }
}
