package calculator;

import java.util.Arrays;
import java.util.List;

public class StringSplitter {

    private static final String DEFAULT_DELIMITERS = ",|:";
    public static final String CUSTOM_DELIMITER_PREFIX = "//";
    public static final String CUSTOM_DELIMITER_SUFFIX = "\n";

    private StringSplitter() {
        throw new UnsupportedOperationException("StringSplitter is a utility class and cannot be instantiated.");
    }

    public static List<String> split(final String inputText) {

        // check for custom delimiter
        if (inputText.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            int customDelimiterEndIndex = inputText.indexOf(CUSTOM_DELIMITER_SUFFIX);
            String customDelimiter = inputText.substring(CUSTOM_DELIMITER_PREFIX.length(), customDelimiterEndIndex);
            String numberSection = inputText.substring(customDelimiterEndIndex + CUSTOM_DELIMITER_SUFFIX.length());
            return Arrays.asList(numberSection.split(customDelimiter));
        }

        // default delimiters
        return Arrays.asList(inputText.split(DEFAULT_DELIMITERS));
    }
}


