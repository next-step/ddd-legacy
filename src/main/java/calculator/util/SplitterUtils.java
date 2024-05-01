package calculator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterUtils {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int TEXT_GROUP = 2;

    public static String[] split(String text) {
        validateSplitText(text);

        Delimiter delimiter = new Delimiter(DEFAULT_DELIMITER);
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            delimiter.addDelimiter(matcher.group(DELIMITER_GROUP));
            String targetText = matcher.group(TEXT_GROUP);
            return delimiter.split(targetText);
        }

        return delimiter.split(text);
    }

    private static void validateSplitText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text is Null. Can't Split");
        }

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text isEmpty. Can't Split");
        }
    }
}
