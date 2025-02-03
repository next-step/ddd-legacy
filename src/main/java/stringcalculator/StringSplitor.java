package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitor {

    private static final String COMMON_DELIMITER_PATTERN = "[,:]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    private static final int CUSTOM_DELIMITER = 1;
    private static final int NUMBER_INPUT = 2;

    public static String[] split(String text) {

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER);
            return m.group(NUMBER_INPUT).split(customDelimiter);
        }

        return text.split(COMMON_DELIMITER_PATTERN);
    }
}
