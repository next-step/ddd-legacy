package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSeparator implements Separator {
    private static final String DEFAULT_SEPARATOR = ",|:";
    private static final int CUSTOM_SEPARATOR_INDEX = 1;
    private static final int NUMBER_INDEX = 2;
    private static final Pattern PATTERN = Pattern.compile("//(.)\\n(.*)");

    @Override
    public Numbers separate(String text) {
        Matcher matcher = PATTERN.matcher(text);
        String separator = findSeparator(matcher);
        String numberText = findNumberText(matcher, text);
        return createNumbers(numberText, separator);
    }

    private String findSeparator(Matcher matcher) {
        matcher.reset();
        if (matcher.find()) {
            return matcher.group(CUSTOM_SEPARATOR_INDEX);
        }
        return DEFAULT_SEPARATOR;
    }

    private String findNumberText(Matcher matcher, String defaultValue) {
        matcher.reset();
        if (matcher.find()) {
            return matcher.group(NUMBER_INDEX);
        }
        return defaultValue;
    }

    private Numbers createNumbers(String numberText, String separator) {
        return new Numbers(numberText.split(separator));
    }
}
