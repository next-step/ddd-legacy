package calculator.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern CUSTOM_PATTERN_COMPILE = Pattern.compile("//(.)\n(.*)");
    private static final String DELIMITER_COMMON_REGEX = ",|:";

    public String[] execute(String text) {
        Matcher matcher = CUSTOM_PATTERN_COMPILE.matcher(text);
        if (matcher.find()) {
            return splitTextByCustomDelimiter(matcher);
        }
        return splitText(text);
    }

    private String[] splitTextByCustomDelimiter(Matcher matcher) {
        return matcher.group(2).split(getCustomDelimiter(matcher));
    }

    private String getCustomDelimiter(Matcher matcher) {
        String customDelimiter = matcher.group(1);
        if ("+".equals(customDelimiter) || "*".equals(customDelimiter)) {
            customDelimiter = String.format("\\%s", customDelimiter);
        }
        return customDelimiter;
    }

    private String[] splitText(String text) {
        return text.split(DELIMITER_COMMON_REGEX);
    }
}
