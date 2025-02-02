package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\\\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int ONLY_NUMBER_STRING_INDEX = 2;
    private static final String DEFAULT_DELIMITERS = ",|:";

    private StringParser() {

    }

    public static String[] splitNumbers(String text) {
        final Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (!m.find()) {
            return text.split(DEFAULT_DELIMITERS);
        }

        final String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
        final String delimiter = String.join("|", DEFAULT_DELIMITERS, customDelimiter);

        final String numberString = m.group(ONLY_NUMBER_STRING_INDEX);
        return numberString.split(delimiter);
    }
}
