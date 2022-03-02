package calculator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern PATTERN_CUSTOM_SEPARATOR = Pattern.compile("//(.)\n(.*)");
    private static final String SEPARATOR_SIGN = ",|:";

    public static String[] separate(String inputText) {
        Matcher m = PATTERN_CUSTOM_SEPARATOR.matcher(inputText);

        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return inputText.split(SEPARATOR_SIGN);
    }
}
