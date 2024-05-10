package calculator;


import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Delimiter {

    public static final String CUSTOM_DELIMITER_PREFIX = "//";
    public static final String CUSTOM_DELIMITER_SUFFIX = "\n";
    private static final String DEFAULT_DELIMITER = "[,:]";

    private final String delimiter;

    private Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public static Delimiter of(String text) {
        if (text.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            int prefixLength = CUSTOM_DELIMITER_PREFIX.length();
            int suffixIndex = text.indexOf(CUSTOM_DELIMITER_SUFFIX);
            String customDelimiter = text.substring(prefixLength, suffixIndex);
            return new Delimiter(combineDelimiters(customDelimiter));
        }

        return new Delimiter(DEFAULT_DELIMITER);
    }

    private static String combineDelimiters(String customDelimiter) {
        return DEFAULT_DELIMITER + "|" + Pattern.quote(customDelimiter);
    }

    public Stream<String> split(String text) {
        return Arrays.stream(text.split(this.delimiter));
    }
}
