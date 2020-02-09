package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern CUSTOM_DELIMITER_REGEX = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";

    public String[] parseStrings(final String text) {
        Matcher m = CUSTOM_DELIMITER_REGEX.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String stringNumbers = m.group(2);
            return parseStrings(stringNumbers, customDelimiter);
        }
        return parseStrings(text, DEFAULT_DELIMITER_REGEX);
    }

    public String[] parseStrings(final String text, final String del) {
        return text.split(del);
    }

}
