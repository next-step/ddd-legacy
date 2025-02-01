package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\\\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int ONLY_NUMBER_STRING_INDEX = 2;
    private static final String DEFAULT_DELIMITERS = ",|:";

    private StringParser() {

    }

    public static String[] splitNumbers(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        String delimiters = DEFAULT_DELIMITERS;

        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            delimiters = delimiters + "|" + customDelimiter;
            text = m.group(ONLY_NUMBER_STRING_INDEX);
        }

        return text.split(delimiters);
    }
}
