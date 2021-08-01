package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokenizer {
    private static final int DELIMITER_MATCHING_GROUP = 1;
    private static final int TEXT_MATCHING_GROUP = 2;
    private static final String DELIMITER_SEPARATOR = "|";

    private final List<String> defaultDelimiters = Arrays.asList(",", ":");
    private final Pattern customDelimiterRegexPattern = Pattern.compile("//(.)\n(.*)");

    public StringOperands tokenize(final String text) {
        final List<String> delimiters = new ArrayList<>(defaultDelimiters);
        final Matcher matcher = customDelimiterRegexPattern.matcher(text);

        String operandsText = text;
        if (matcher.find()) {
            delimiters.add(matcher.group(DELIMITER_MATCHING_GROUP));
            operandsText = matcher.group(TEXT_MATCHING_GROUP);
        }

        final String delimiterRegex = String.join(DELIMITER_SEPARATOR, delimiters);
        return StringOperands.of(operandsText, delimiterRegex);
    }
}
