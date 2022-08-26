package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final Pattern customDelimiterPattern = Pattern.compile("//(.)\n(.*)");
    private final String DELIMITER_COMMON_REGEX = ",|:";

    public String[] execute(String text) {
        Matcher matcher = customDelimiterPattern.matcher(text);
        if (matcher.find()) {
            return splitTextByCustomDelimiter(matcher);
        }
        return splitText(text);
    }

    private String[] splitTextByCustomDelimiter(Matcher m) {
        String customDelimiter = m.group(1);
        if ("+".equals(customDelimiter) || "*".equals(customDelimiter)) {
            customDelimiter = String.format("\\%s", customDelimiter);
        }
        return m.group(2).split(customDelimiter);
    }

    private String[] splitText(String text) {
        return text.split(DELIMITER_COMMON_REGEX);
    }
}
