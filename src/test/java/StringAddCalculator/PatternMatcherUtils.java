package StringAddCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcherUtils {

    public static final String CUSTOM_REGEX = "//(.)\n(.*)";
    public static final String REGEX = "[,:]";

    public static String[] customDelimit(String text) {
        Matcher m = Pattern.compile(CUSTOM_REGEX).matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split(REGEX);
    }
}
