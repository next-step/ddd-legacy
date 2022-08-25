package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenUtils {

    private final static String DEFAULT_TOKEN_REGEX = ",|:";
    private final static String CUSTOM_TOKEN_REGEX = "//(.)\n(.*)";
    private final static Pattern CUSTOM_TOKEN_PATTERN = Pattern.compile(CUSTOM_TOKEN_REGEX);
    private final static int DELIMITER_INDEX = 1;
    private final static int SEPARATOR_INDEX = 2;

    public static String[] tokenizer(String word) {
        final Matcher tokenMatcher = CUSTOM_TOKEN_PATTERN.matcher(word);

        if (tokenMatcher.find()) {
            String customDelimiter = tokenMatcher.group(DELIMITER_INDEX);
            return tokenMatcher.group(SEPARATOR_INDEX).split(customDelimiter);
        }

        return word.split(DEFAULT_TOKEN_REGEX);
    }
}
