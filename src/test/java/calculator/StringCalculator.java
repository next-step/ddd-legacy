package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class StringCalculator {

    private static final String DELIMITER_REGEX = ",|:";
    private static final String DELIMITER_PLACEHOLDER = "delimiter";
    private static final String TOKENS_PLACEHOLDER = "tokens";
    private static final String VALID_INPUT_REGEX = "(?://(?<" + DELIMITER_PLACEHOLDER + ">.)\\n)?(?<" + TOKENS_PLACEHOLDER + ">.*)";
    private static final Pattern VALID_INPUT_PATTERN = Pattern.compile(VALID_INPUT_REGEX);

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        var a = VALID_INPUT_PATTERN.matcher(text);

        if (!a.find()) {
            throw new IllegalArgumentException("올바르지 않은 형태입니다.");
        }

        var customDelimiter = a.group(DELIMITER_PLACEHOLDER);

        var delimiterRegex = DELIMITER_REGEX;

        if (customDelimiter != null) {
            delimiterRegex = delimiterRegex + "|" + customDelimiter;
        }

        String[] tokens = a.group(TOKENS_PLACEHOLDER).split(delimiterRegex);

        return Arrays.stream(tokens)
                .mapToInt(this::parseIntegerFromToken)
                .sum();
    }

    private int parseIntegerFromToken(String token) {
        return parseInt(token);
    }
}
