package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDelimiter implements Delimiter {

    private static final Pattern USER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final int CUSTOMS_DELIMITER_GROUP = 1;
    private static final int COUSTOMS_TEXT_GROUP = 2;

    @Override
    public String[] parse(final String text) {
        Matcher m = USER_PATTERN.matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(CUSTOMS_DELIMITER_GROUP);
            return m.group(COUSTOMS_TEXT_GROUP).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }
}
