package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final Integer CUSTOM_DELIMITER_GROUP = 1;
    private static final Integer CUSTOM_TEXT_GROUP = 2;
    private static final String DEFAULT_DELIMITERS = "[,:]";

    public String[] split(String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_GROUP);
            return m.group(CUSTOM_TEXT_GROUP).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITERS);
    }
}
