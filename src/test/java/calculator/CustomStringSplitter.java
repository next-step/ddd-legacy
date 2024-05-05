package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CustomStringSplitter implements StringSplitter {

    private static final String SUPPORT_PATTERN_REGEX = "//(.)\\n(.*)";
    private static final Pattern SUPPORT_PATTERN = Pattern.compile(SUPPORT_PATTERN_REGEX);

    private static final String REGEX = "[,|:|%s]";

    private static final int DELIMITER_INDEX = 1;
    private static final int REAL_VALUE_REGEX = 2;

    @Override
    public String[] split(final String value) {
        if (!support(value)) {
            throw new IllegalStateException();
        }
        final Matcher matcher = SUPPORT_PATTERN.matcher(value);
        if (!matcher.find()) {
            throw new IllegalStateException();
        }
        final String realValue = matcher.group(REAL_VALUE_REGEX);
        final String delimiter = matcher.group(DELIMITER_INDEX);
        return realValue.split(String.format(REGEX, delimiter));
    }

    @Override
    public boolean support(final String value) {
        return SUPPORT_PATTERN.matcher(value).matches();
    }
}
