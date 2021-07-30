package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {
    private static final String DEFAULT_PATTERN = ",|:";
    private static final String CUSTOM_PATTERN = "//(.)\n(.*)";
    private static final int PATTERN_GROUP = 1;
    private static final int TEXT_GROUP = 2;

    public static String[] textToTokens(String text) {
        Matcher m = Pattern.compile(CUSTOM_PATTERN).matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(PATTERN_GROUP);
            return m.group(TEXT_GROUP).split(customDelimiter);
        }
        return text.split(DEFAULT_PATTERN);
    }
}
