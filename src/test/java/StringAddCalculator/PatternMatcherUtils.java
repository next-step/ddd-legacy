package StringAddCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcherUtils {

    public static final String CUSTOM_REGEX = "//(.)\n(.*)";
    public static final String DEFAULT_REGEX = "[,:]";
    public static final int DELIMITER_GROUP = 1;
    public static final int TEXT_GROUP = 2;
    private static final Pattern pattern = Pattern.compile(CUSTOM_REGEX);

    public static String[] customDelimit(String text) {
        Matcher m = pattern.matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(DELIMITER_GROUP);
            return m.group(TEXT_GROUP).split(customDelimiter);
        }

        return text.split(DEFAULT_REGEX);
    }
}
