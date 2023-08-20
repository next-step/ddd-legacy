package calculator.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Separate {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DELIMITER_PATTERN = "[,:]";

    public String[] parser(final String input) {
        final Matcher customMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);

        if (customMatcher.find()) {
            final String customDelimiter = customMatcher.group(1);
            return customMatcher.group(2).split(customDelimiter);
        }

        return input.split(DELIMITER_PATTERN);
    }

}
