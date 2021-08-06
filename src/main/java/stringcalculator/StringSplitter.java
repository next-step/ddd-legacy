package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {

    private static final Pattern REGEX_CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String REGEX_DEFAULT_DELIMITER = "[,:]";
    private static final int MATCHER_FIRST_GROUP_INDEX = 1;
    private static final int MATCHER_SECOND_GROUP_INDEX = 2;
    private static final SplitTexts EMPTY_SPLIT_TEXTS = new SplitTexts();

    public static SplitTexts split(final String text) {
        if (text == null || text.isEmpty()) {
            return EMPTY_SPLIT_TEXTS;
        }

        SplitTexts customDelimiterSplitTexts = splitByCustomDelimiter(text);

        return !customDelimiterSplitTexts.isEmpty()
            ? customDelimiterSplitTexts
            : splitByDefaultDelimiter(text);
    }

    private static SplitTexts splitByCustomDelimiter(final String text) {
        Matcher m = REGEX_CUSTOM_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(MATCHER_FIRST_GROUP_INDEX);
            String[] tokens = m.group(MATCHER_SECOND_GROUP_INDEX).split(customDelimiter);
            return new SplitTexts(tokens);
        }
        return EMPTY_SPLIT_TEXTS;
    }

    private static SplitTexts splitByDefaultDelimiter(final String text) {
        return new SplitTexts(text.split(REGEX_DEFAULT_DELIMITER));
    }
}
