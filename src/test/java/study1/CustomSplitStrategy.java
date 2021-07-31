package study1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitStrategy implements SplitStrategy {

    private static final Pattern CUSTOM_DELIM_PREFIX = Pattern.compile("^//(.)\n(.*)");
    private static final int DELIMITER_INDEX = 1;
    private static final int CONTENT_INDEX = 2;

    public static boolean applicable(final String text) {
        return CUSTOM_DELIM_PREFIX.matcher(text).matches();
    }

    @Override
    public String[] split(final String text) {
        final Matcher matcher = CUSTOM_DELIM_PREFIX.matcher(text);
        if (!matcher.matches()) {
            throw new RuntimeException("텍스트에 커스텀 구분자가 존재하지 않습니다.");
        };

        final String delimiter = matcher.group(DELIMITER_INDEX);
        return matcher.group(CONTENT_INDEX).split(delimiter);
    }
}
