package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterSplit {
    private static final String DEFAULT_DELIMITER = ",|:";

    private static final String CUSTOM_DELIMITER = "//(.)\\n(.*)";

    public String[] split(final String text) {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER).matcher(text);

        if (matcher.find()) {
            return matcher.group(2).split(matcher.group(1));
        } else {
            return text.split(DEFAULT_DELIMITER);
        }
    }
}
