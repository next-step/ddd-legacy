package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String DEFAULT_SEPARATOR = "[,:]";

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");
    private static final Pattern CUSTOM_SEPARATOR_PATTERN = Pattern.compile("//(.)\n(.*)");

    static boolean isBlank(final String text) {
        return text == null || text.isEmpty();
    }

    static int parsePositiveInteger(final String text) {
        if (!NUMBER_PATTERN.matcher(text).matches()) {
            throw new RuntimeException();
        }

        return Integer.parseInt(text);
    }

    static String[] parseTokens(String text) {
        Matcher customPatternMatcher = CUSTOM_SEPARATOR_PATTERN.matcher(text);
        if (customPatternMatcher.find()) {
            String parsedText = customPatternMatcher.group(2);
            String separator = customPatternMatcher.group(1);

            return parsedText.split(separator);
        }

        return text.split(DEFAULT_SEPARATOR);
    }

}
