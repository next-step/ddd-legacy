package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextInputHandler {

    private static final String CUSTOM_DELIMITER_SYNTAX = "//(.*)\n(.*)";
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMITER_SYNTAX);

    public String[] tokenize(String text) {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = Pattern.quote(matcher.group(1));
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITERS);
    }

    public boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}