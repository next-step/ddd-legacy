package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterUtils {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static String[] split(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        Delimiter delimiter = new Delimiter(DEFAULT_DELIMITER);
        if (matcher.find()) {
            delimiter.addDelimiter(matcher.group(1));
            String targetText = matcher.group(2);
            return delimiter.split(targetText);
        }

        return delimiter.split(text);
    }
}
