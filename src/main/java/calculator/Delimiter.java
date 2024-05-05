package calculator;


import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Delimiter {

    public static final String CUSTOM_DELIMITER_PREFIX = "//";
    public static final String CUSTOM_DELIMITER_SUFFIX = "\n";

    private static final String DEFAULT_DELIMITER = "[,:]";
    private final String delimiter;

    public Delimiter(String text) {
        if (text.startsWith(CUSTOM_DELIMITER_PREFIX)) {
            int prefixLength = CUSTOM_DELIMITER_PREFIX.length();
            int suffixIndex = text.indexOf(CUSTOM_DELIMITER_SUFFIX);
            String customDelimiter = text.substring(prefixLength, suffixIndex);
            this.delimiter = combineDelimiters(customDelimiter);
            return;
        }

        this.delimiter = DEFAULT_DELIMITER;
    }

    private String combineDelimiters(String customDelimiter) {
        return DEFAULT_DELIMITER + "|" + Pattern.quote(customDelimiter);
    }

    public Stream<String> split(String text) {
        return Arrays.stream(text.split(this.delimiter));
    }
}
