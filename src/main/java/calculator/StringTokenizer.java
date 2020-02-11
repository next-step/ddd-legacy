package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringTokenizer {

    private static final Pattern CUSTOM_DELIMITER_FIND_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String[] DEFAULT_DELIMITER = {",", ":"};

    public static String[] tokenize(String text) {
        List<String> delimiters = Arrays.stream(DEFAULT_DELIMITER)
                .collect(Collectors.toList());

        Matcher matcher = CUSTOM_DELIMITER_FIND_PATTERN.matcher(text);
        if (matcher.find()) {
            delimiters.add(matcher.group(1));
            text = matcher.group(2);
        }

        return text.split(getRegexOfDelimiters(delimiters));
    }

    private static String getRegexOfDelimiters(List<String> delimiters) {
        return delimiters.stream()
                .collect(Collectors.joining("|"));
    }
}
