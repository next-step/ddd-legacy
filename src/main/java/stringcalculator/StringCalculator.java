package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = "[,:]";
    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String NEWLINE = "\n";
    private static final String CUSTOM_DELIMITER_REGEX = CUSTOM_DELIMITER_PREFIX + "(.)" + NEWLINE + "(.*)";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);

    public int add(String input) {
        if (isNullOrEmpty(input)) {
            return 0;
        }

        if (input.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            return sumByCustomDelimiter(input);
        }

        return sumByDefaultDelimiters(input);
    }

    private boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private int sumByDefaultDelimiters(String input) {
        String[] tokens = input.split(DEFAULT_DELIMITERS);
        return sumTokens(tokens);
    }

    private int sumByCustomDelimiter(String input) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] tokens = matcher.group(2).split(customDelimiter);
            return sumTokens(tokens);
        }
        return 0;
    }

    private int sumTokens(String[] tokens) {
        return Arrays.stream(tokens)
                .filter(token -> !token.isEmpty())
                .mapToInt(token -> PositiveNumber.parseToken(token).getNumber())
                .sum();
    }
}
