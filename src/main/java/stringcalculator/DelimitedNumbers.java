package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DelimitedNumbers {

    private static final String DEFAULT_DELIMITER_REGEX = ",|:";

    private static final String CUSTOM_DELIMITER_PATTERN_STR = "//(.)\n(.*)";

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_PATTERN_STR);

    private final String delimiter;

    private final String numbers;

    public DelimitedNumbers(String text) {
        this.delimiter = extractDelimiter(text);
        this.numbers = extractNumbers(text, delimiter);
    }

    private String extractDelimiter(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.matches()) {
            return Pattern.quote(matcher.group(1));
        }
        return DEFAULT_DELIMITER_REGEX;
    }

    private String extractNumbers(String text, String delimiter) {
        if (delimiter.equals(DEFAULT_DELIMITER_REGEX)) {
            return text;
        }

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);

        return matcher.matches() ? matcher.group(2) : text;
    }

    public Stream<String> getNumbersStream() {
        return Arrays.stream(numbers.split(delimiter));
    }

}