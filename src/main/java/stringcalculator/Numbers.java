package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Numbers {
    private static final String ERROR_MINUS_NUMBER_MESSAGE = "마이너스 숫자는 불가능합니다.";
    private static final String DEFAULT_PATTERN = ",|:";
    private static final String CUSTOM_PATTERN = "//(.)\n(.*)";
    private static final int PATTERN_GROUP = 1;
    private static final int TEXT_GROUP = 2;

    public static int[] textToNumbers(String text) {
        return Arrays.stream(textToTokens(text))
                .mapToInt(Numbers::stringToInt)
                .toArray();
    }

    private static String[] textToTokens(String text) {
        Matcher m = Pattern.compile(CUSTOM_PATTERN).matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(PATTERN_GROUP);
            return m.group(TEXT_GROUP).split(customDelimiter);
        }
        return text.split(DEFAULT_PATTERN);
    }

    private static int stringToInt(String text) {
        int result = Integer.parseInt(text);

        if (result < 0) {
            throw new IllegalArgumentException(ERROR_MINUS_NUMBER_MESSAGE);
        }

        return result;
    }
}
