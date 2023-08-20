package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    static final Pattern patten = Pattern.compile("//(.)\n(.*)");

    public String[] toNumbers(String text) {

        final int CUSTOM_DELIMITER = 1;
        final int TARGET = 2;

        String[] result = {};
        if (text.contains(",") | text.contains(":")) {
            result = text.split(",|:");
            return result;
        }

        Matcher matcher = patten.matcher(text);
        if (matcher.find()) {
            String customerDelimiter = matcher.group(CUSTOM_DELIMITER);
            result = matcher.group(TARGET).split(customerDelimiter);
            return result;
        }

        return result;
    }
}
