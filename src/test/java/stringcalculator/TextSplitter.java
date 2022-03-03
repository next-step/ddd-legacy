package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitter {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER_REGEX = ",|:";
    private static final int DELIMITER_GROUP_INDEX = 1;
    private static final int TEXT_GROUP_INDEX = 2;

    public static Numbers split(final String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if(hasCustomDelimiter(matcher)) {
            String customDelimiter = matcher.group(DELIMITER_GROUP_INDEX);
            String[] numbers = matcher.group(TEXT_GROUP_INDEX)
                    .split(customDelimiter);

            return Numbers.of(numbers);
        }

        return Numbers.of(text.split(DEFAULT_DELIMITER_REGEX));
    }

    private static boolean hasCustomDelimiter(Matcher matcher) {
        return matcher.find();
    }

}
