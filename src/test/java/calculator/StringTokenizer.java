package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenizer {
    private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(",", ":");
    private static final Pattern CUSTOM_DELIMITER_REGEX_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_MATCHING_GROUP = 1;
    private static final int TEXT_MATCHING_GROUP = 2;
    private static final String DELIMITER_SEPARATOR = "|";

    public StringOperands tokenize(final String text) {
        final List<String> delimiters = new ArrayList<>(DEFAULT_DELIMITERS);
        final Matcher matcher = CUSTOM_DELIMITER_REGEX_PATTERN.matcher(text);

        String operandsText = text;
        if (matcher.find()) {
            delimiters.add(matcher.group(DELIMITER_MATCHING_GROUP));
            operandsText = matcher.group(TEXT_MATCHING_GROUP);
        }

        final String delimiterRegex = String.join(DELIMITER_SEPARATOR, delimiters);
        return StringOperands.of(operandsText, delimiterRegex);
    }
}
