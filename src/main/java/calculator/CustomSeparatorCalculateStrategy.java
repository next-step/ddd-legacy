package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSeparatorCalculateStrategy extends AbstractCalculateStrategy {

    private static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER = 1;
    private static final int TARGET_TEXT = 2;

    @Override
    public boolean isTarget(final String text) {
        return CUSTOM_SEPARATOR_PATTERN.matcher(text).find();
    }

    @Override
    public int calculate(final String text) {
        Matcher matcher = getMatcher(text);
        return calculateWithDelimiter(matcher.group(TARGET_TEXT), matcher.group(CUSTOM_DELIMITER));
    }

    private Matcher getMatcher(final String text) {
        Matcher matcher = CUSTOM_SEPARATOR_PATTERN.matcher(text);
        matcher.find();
        return matcher;
    }

}

