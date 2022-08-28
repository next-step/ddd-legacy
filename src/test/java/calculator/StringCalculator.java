package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile(DEFAULT_DELIMITER_REGEX);

    public int add(String text) {
        if (text == null) {
            return 0;
        }

        final var expression = new AdditionExpression(text);

        final var delimiterPattern = compileDelimiterPattern(expression.getCustomDelimiter());

        if (expression.isTokensBlank()) {
            return 0;
        }

        final String[] tokens = splitTokensByDelimiter(expression.getTokens(), delimiterPattern);

        return Arrays.stream(tokens)
                .mapToInt(this::parsePositiveIntFromToken)
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

    private int parsePositiveIntFromToken(String token) {
        final var integer = parseInt(token);

        if (integer < 0) {
            throw new RuntimeException("음수는 계산할 수 없습니다.");
        }

        return integer;
    }
}
