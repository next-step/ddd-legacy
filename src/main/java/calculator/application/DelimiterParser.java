package calculator.application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER);
    public static final int CUSTOM_DELIMITER_GROUP_INDEX = 1;
    public static final int CUSTOM_DELIMITER_STRING_INDEX = 2;

    private DelimiterParser() {
    }

    public static String[] splitText(String inputText) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(inputText);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_GROUP_INDEX);
            return matcher.group(CUSTOM_DELIMITER_STRING_INDEX).split(customDelimiter);
        }
        return inputText.split(DEFAULT_DELIMITER);
    }
}
