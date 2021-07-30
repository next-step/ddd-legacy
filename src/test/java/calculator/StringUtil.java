package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int ONE = 1;
    private static final int TWO = 2;

    public static String[] split(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(ONE);
            return matcher.group(TWO).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }
}
