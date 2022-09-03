package kitchenpos.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern regex = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = ",:";

    public static String[] filterTextByDelimiter(String text) {

        Matcher m = regex.matcher(text);

        String originText = text;
        String delimiters = DEFAULT_DELIMITER;

        if (m.find()) {
            originText = m.group(2);
            delimiters = delimiters + m.group(1);
        }

        return originText.split(delimiters(delimiters));
    }

    private static String delimiters(String delimiter) {
        return "[" + delimiter + "]";
    }

}
