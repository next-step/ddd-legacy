package StringAddCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcher {

    private static final String CUSTOM_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_REGEX = "[,:]";
    private static final int DELIMITER_GROUP = 1;
    private static final int TEXT_GROUP = 2;
    private static final Pattern pattern = Pattern.compile(CUSTOM_REGEX);

    private final Matcher m;

    public PatternMatcher(String text) {
        m = pattern.matcher(text);
    }

    public String[] customDelimit(String text) {

        if (m.find()) {
            String customDelimiter = m.group(DELIMITER_GROUP);
            return m.group(TEXT_GROUP).split(customDelimiter);
        }

        return text.split(DEFAULT_REGEX);
    }
}
