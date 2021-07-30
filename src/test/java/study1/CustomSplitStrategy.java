package study1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitStrategy implements SplitStrategy {

    private static final Pattern CUSTOM_DELIM_PREFIX = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int CONTENT_INDEX = 2;

    public static boolean applicable(final String text) {
        return CUSTOM_DELIM_PREFIX.matcher(text).find();
    }

    @Override
    public String[] split(final String text) {
        if (!applicable(text)) {
            throw new RuntimeException();
        }

        final Matcher matcher = CUSTOM_DELIM_PREFIX.matcher(text);
        final String delimiter = matcher.group(DELIMITER_INDEX);
        return matcher.group(CONTENT_INDEX).split(delimiter);
    }
}
