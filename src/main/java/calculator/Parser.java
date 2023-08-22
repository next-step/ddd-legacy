package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final Pattern CUSTOM_DELIMITER_PATTREN = Pattern.compile(CUSTOM_DELIMITER_REGEX);
    private static final String DEFAULT_DELIMITER = ",|:";

    public String[] findTokens(final String text) {
        Matcher m = CUSTOM_DELIMITER_PATTREN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(MatcherGroup.CUSTOM_DELIMITER.getGroup());
            return m.group(MatcherGroup.STRING_TO_SPLIT.getGroup()).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }
}
