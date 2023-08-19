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

    public String value;

    public Delimiter(final String text) {
        this.value = extractDelimiter(text);
    }

    public String getValue() {
        return value;
    }

    public List<Number> extractNumbers(final String text) {
        String numberText = extractNumberText(text);
        String[] tokens = numberText.split(value);
        return Arrays.stream(tokens)
            .map(Number::new)
            .collect(Collectors.toList());
    }

    private String extractDelimiter(final String text) {
        if (text == null || text.isEmpty()) {
            return DEFAULT_DELIMITER;
        }
        return extractCustomDelimiter(text)
            .map(customDelimiter -> DEFAULT_DELIMITER + "|" + customDelimiter)
            .orElse(DEFAULT_DELIMITER);
    }

    private Optional<String> extractCustomDelimiter(final String text) {
        final int CUSTOM_DELIMITER_GROUP = 1;
        Matcher customDelimiter = CUSTOM_DELIMITER_PATTERN.matcher(text);

        if (customDelimiter.find()) {
            return Optional.ofNullable(customDelimiter.group(CUSTOM_DELIMITER_GROUP));
        }
        return Optional.empty();
    }

    private String extractNumberText(final String text) {
        final int NUMBER_TOKEN_GROUP = 2;
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(NUMBER_TOKEN_GROUP) : text;
    }

}
