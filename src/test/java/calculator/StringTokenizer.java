package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenizer {
    private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(",", ":");
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final int DELIMITER_MATCHING_GROUP = 1;
    private static final int TEXT_MATCHING_GROUP = 2;
    private static final String DELIMITER_SEPARATOR = "|";

    private final String[] tokens;

    private StringTokenizer(final String text) {
        final List<String> delimiters = new ArrayList<>(DEFAULT_DELIMITERS);
        final Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(text);

        String operandText = text;
        if (matcher.find()) {
            delimiters.add(matcher.group(DELIMITER_MATCHING_GROUP));
            operandText = matcher.group(TEXT_MATCHING_GROUP);
        }

        final String delimiterRegex = String.join(DELIMITER_SEPARATOR, delimiters);
        this.tokens = operandText.split(delimiterRegex);
    }

    public static StringTokenizer of(final String text) {
        return new StringTokenizer(text);
    }

    public String[] tokenize() {
        return tokens;
    }

    public boolean isEmpty() {
        return tokens.length == 1 && tokens[0].isEmpty();
    }
}
