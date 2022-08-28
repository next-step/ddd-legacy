package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile(DEFAULT_DELIMITER_REGEX);
    private static final String DELIMITER_PLACEHOLDER = "delimiter";
    private static final String TOKENS_PLACEHOLDER = "tokens";
    private static final String VALID_INPUT_REGEX = "(?://(?<" + DELIMITER_PLACEHOLDER + ">.)\\n)?(?<" + TOKENS_PLACEHOLDER + ">.*)";
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile(VALID_INPUT_REGEX);

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        final var input = getValidMatcher(text);

        final var delimiterPattern = compileDelimiterPattern(input.group(DELIMITER_PLACEHOLDER));

        final String[] tokens = splitTokensByDelimiter(input.group(TOKENS_PLACEHOLDER), delimiterPattern);

        return Arrays.stream(tokens)
                .mapToInt(this::parseIntegerFromToken)
                .sum();
    }

    private String[] splitTokensByDelimiter(String tokensPart, Pattern delimiterRegex) {
        return delimiterRegex.split(tokensPart);
    }

    private Matcher getValidMatcher(String text) {
        final var matcher = VALID_INPUT_PATTERN.matcher(text);

        if (!matcher.find()) {
            throw new IllegalArgumentException("올바르지 않은 형태입니다.");
        }

        return matcher;
    }

    private Pattern compileDelimiterPattern(String customizedDelimiter) {
        if (customizedDelimiter == null) {
            return DEFAULT_DELIMITER_PATTERN;
        }

        return Pattern.compile(DEFAULT_DELIMITER_REGEX + "|" + customizedDelimiter);
    }

    private int parseIntegerFromToken(String token) {
        return parseInt(token);
    }
}
