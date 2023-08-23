package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterSplit {
    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,:]");
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public String[] split(final String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if (matcher.find()) {
            return matcher.group(2).split(matcher.group(1));
        } else {
            return DEFAULT_DELIMITER_PATTERN.split(text);
        }
    }
}
