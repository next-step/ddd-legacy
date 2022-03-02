package stringcalculator;

import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");

    static boolean isBlank(final String text) {
        return text == null || text.isEmpty();
    }

    static int parsePositiveInteger(final String text) {
        if (!NUMBER_PATTERN.matcher(text).matches()) {
            throw new RuntimeException();
        }

        return Integer.parseInt(text);
    }

}
