package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record DelimiterParser() {

    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public String[] parse(String input) {
        if (input == null || input.isBlank()) {
            return new String[0];
        }

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.matches()) {
            var customDelimiter = matcher.group(1);
            var numbers = matcher.group(2);
            return numbers.split(Pattern.quote(customDelimiter));
        }

        return input.split(DEFAULT_DELIMITERS);
    }
}
