package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterUtils {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static String[] split(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = makeCustomDelimiter(matcher.group(1));
            String targetText = matcher.group(2);
            return targetText.split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private static String makeCustomDelimiter(String delimiter) {
        return DEFAULT_DELIMITER + "|" + delimiter;
    }
}
