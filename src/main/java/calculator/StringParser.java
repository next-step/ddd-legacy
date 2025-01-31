package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\\\n(.*)");

    private StringParser() {

    }

    public static String[] splitNumbers(String text) {
        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        String delimiters = ",|:";

        if (m.find()) {
            String customDelimiter = m.group(1);
            delimiters = delimiters + "|" + customDelimiter;
            text = m.group(2);
        }

        return text.split(delimiters);
    }
}

