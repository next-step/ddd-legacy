package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
    private static final String DEFAULT_SEPARATORS = "[,:]";
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int NUMBER_INDEX = 2;

    public static String[] parseNumbers(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(DELIMITER_INDEX);
            return matcher.group(NUMBER_INDEX).split(customDelimiter);
        }
        return text.split(DEFAULT_SEPARATORS);
    }
}
