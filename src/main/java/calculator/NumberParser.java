package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberParser {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final Pattern PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);
    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int TARGET_TEXT_INDEX = 2;

    public static Numbers parse(String str) {
        return Numbers.from(extractNumbers(str));
    }

    private static String[] extractNumbers(String str) {
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            return m.group(TARGET_TEXT_INDEX).split(customDelimiter);
        }

        return str.split(DEFAULT_DELIMITER_REGEX);
    }
}
