package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Delimiter {

    public static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_GROUP = 1;
    private static final int NUMBER_TOKEN_GROUP = 2;

    private Delimiter() {
    }

    public static List<PositiveNumber> extractNumbers(final String text) {
        String delimiter = extractDelimiter(text);
        String numberText = extractNumberText(text);
        String[] tokens = numberText.split(delimiter);
        return Arrays.stream(tokens)
            .map(PositiveNumber::new)
            .collect(Collectors.toList());
    }

    private static String extractDelimiter(final String text) {
        if (text == null || text.isEmpty()) {
            return DEFAULT_DELIMITER;
        }
        return extractCustomDelimiter(text)
            .map(customDelimiter -> DEFAULT_DELIMITER + "|" + customDelimiter)
            .orElse(DEFAULT_DELIMITER);
    }

    private static Optional<String> extractCustomDelimiter(final String text) {
        Matcher customDelimiter = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if (customDelimiter.find()) {
            return Optional.ofNullable(customDelimiter.group(CUSTOM_DELIMITER_GROUP));
        }
        return Optional.empty();
    }

    private static String extractNumberText(final String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(NUMBER_TOKEN_GROUP) : text;
    }

}
