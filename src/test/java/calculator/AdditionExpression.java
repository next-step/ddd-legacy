package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdditionExpression {
    private static final String CUSTOM_DELIMITER_PLACEHOLDER = "delimiter";
    private static final String TOKENS_PLACEHOLDER = "tokens";
    private static final String VALID_INPUT_REGEX = "(?://(?<" + CUSTOM_DELIMITER_PLACEHOLDER + ">.)\\n)?(?<" + TOKENS_PLACEHOLDER + ">.*)";
    public static final Pattern VALID_INPUT_PATTERN = Pattern.compile(VALID_INPUT_REGEX);

    private final String customDelimiter;
    private final String tokens;

    public AdditionExpression(String text) {
        if (text == null) {
            throw new IllegalArgumentException("null을 입력할 수 없습니다.");
        }

        final var matcher = match(text);

        this.customDelimiter = matcher.group(CUSTOM_DELIMITER_PLACEHOLDER);
        this.tokens = matcher.group(TOKENS_PLACEHOLDER);
    }

    public String getTokens() {
        return tokens;
    }

    public boolean isTokensBlank() {
        return tokens.isBlank();
    }

    private Matcher match(String text) {
        final var matcher = VALID_INPUT_PATTERN.matcher(text);

        if (!matcher.find()) {
            throw new IllegalArgumentException("올바르지 않은 형태입니다.");
        }

        return matcher;
    }

    public String getCustomDelimiter() {
        return customDelimiter;
    }
}
