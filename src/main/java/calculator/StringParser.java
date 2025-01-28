package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {

    private StringParser() {

    }

    public static String[] splitNumbers(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        String delimiters = ",|:";

        if (m.find()) {
            String customDelimiter = m.group(1);
            delimiters = delimiters + "|" + customDelimiter;
            text = m.group(2);
        }

        return text.split(delimiters);
    }
}

