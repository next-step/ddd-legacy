package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record DelimiterParser() {

    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;

    public String[] parse(String input) {
        if (input == null || input.isBlank()) {
            return new String[0];
        }

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.matches()) {
            var customDelimiter = matcher.group(CUSTOM_DELIMITER_GROUP);
            var numbers = matcher.group(NUMBERS_GROUP);
            return numbers.split(Pattern.quote(customDelimiter));
        }

        return input.split(DEFAULT_DELIMITERS);
    }
}
