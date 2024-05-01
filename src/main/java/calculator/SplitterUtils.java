package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitterUtils {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static String[] split(String text) {
        Matcher matcher = DEFAULT_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = makeCustomDelimiter(matcher.group(1));
            return matcher.group(2).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private static String makeCustomDelimiter(String delimiter){
        return DEFAULT_DELIMITER + "|" + delimiter;
    }
}
