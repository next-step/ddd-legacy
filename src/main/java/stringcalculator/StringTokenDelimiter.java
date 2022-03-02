package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenDelimiter {

    private static final String TOKEN_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int CUSTOM_DELIMITER_INPUT_INDEX = 2;

    private StringTokenDelimiter() {
    }

    public static String[] split(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            return splitByCustomDelimiter(matcher);
        }
        return text.split(TOKEN_DELIMITER);
    }

    private static String[] splitByCustomDelimiter(Matcher matcher) {
        String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
        return matcher.group(CUSTOM_DELIMITER_INPUT_INDEX).split(customDelimiter);
    }
}
