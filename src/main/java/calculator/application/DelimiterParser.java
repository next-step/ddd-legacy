package calculator.application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER);

    public static String[] splitText(String inputText) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(inputText);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return inputText.split(DEFAULT_DELIMITER);
    }
}
