package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_REGEX = ",|:";

    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";

    public int add(final String text) {
        if (isEmpty(text)) {
            return 0;
        }

        String delimiter = extractDelimiter(text);
        String numbers = extractNumbers(text, delimiter);

        return calculateSum(numbers, delimiter);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String extractDelimiter(String text) {
        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(text);
        if (matcher.matches()) {
            return Pattern.quote(matcher.group(1));
        }
        return DEFAULT_DELIMITER_REGEX;
    }

    private String extractNumbers(String text, String delimiter) {
        if (delimiter.equals(DEFAULT_DELIMITER_REGEX)) {
            return text;
        }

        Matcher matcher = Pattern.compile(CUSTOM_DELIMITER_PATTERN)
                .matcher(text);

        return matcher.matches() ? matcher.group(2) : text;
    }

    private int calculateSum(String numbers, String delimiter) {
        return Arrays.stream(numbers.split(delimiter))
                .mapToInt(this::validateAndParse)
                .sum();
    }

    private int validateAndParse(String number) {
        int parsedNumber = Integer.parseInt(number.trim());
        if (parsedNumber < 0) {
            throw new RuntimeException("음수는 허용되지 않습니다.");
        }
        return parsedNumber;
    }
}
