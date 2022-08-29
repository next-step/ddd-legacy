package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdditionExpression {
    private static final String CUSTOM_DELIMITER_PLACEHOLDER = "delimiter";
    private static final String TOKENS_PLACEHOLDER = "tokens";
    private static final String VALID_INPUT_REGEX = "(?://(?<" + CUSTOM_DELIMITER_PLACEHOLDER + ">.)\\n)?(?<" + TOKENS_PLACEHOLDER + ">.*)";
    public static final Pattern VALID_INPUT_PATTERN = Pattern.compile(VALID_INPUT_REGEX);
    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile(DEFAULT_DELIMITER_REGEX);
    private static final String[] EMPTY_TOKENS = {};

    private final Pattern delimiter;
    private final String tokens;

    public AdditionExpression(String text) {
        if (text == null) {
            throw new IllegalArgumentException("null을 입력할 수 없습니다.");
        }

        final var matcher = match(text);

        this.delimiter = compileDelimiterPattern(matcher.group(CUSTOM_DELIMITER_PLACEHOLDER));
        this.tokens = matcher.group(TOKENS_PLACEHOLDER);
    }

    private Matcher match(String text) {
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

    public String[] splitTokensByDelimiter() {
        if (tokens.isBlank()) {
            return EMPTY_TOKENS;
        }
        return delimiter.split(tokens);
    }
}
