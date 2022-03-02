package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {

    private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(",", ":");
    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String NEW_LINE = "\n";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_PREFIX + "(.)");

    public PositiveNumber add(String text) {
        if (text == null || text.isEmpty()) {
            return PositiveNumber.ZERO;
        }

        final List<String> customDelimiters = findAllCustomDelimiters(text);

        final String numbersText = parseNumbers(text);

        final String allDelimitersRegex = Stream.concat(
            DEFAULT_DELIMITERS.stream(),
            customDelimiters.stream()
        ).collect(Collectors.joining("|", "[", "]"));

        return Arrays.stream(numbersText.split(allDelimitersRegex))
            .map(Integer::parseInt)
            .map(PositiveNumber::new)
            .reduce(PositiveNumber.ZERO, PositiveNumber::plus);
    }

    private String parseNumbers(String text) {
        final String[] splitText = text.split(NEW_LINE);
        return splitText[splitText.length - 1];
    }

    private List<String> findAllCustomDelimiters(String text) {
        return Arrays.stream(text.split(NEW_LINE))
            .filter(it -> CUSTOM_DELIMITER_PATTERN.matcher(it).matches())
            .map(it -> it.replaceAll(CUSTOM_DELIMITER_PREFIX, ""))
            .collect(Collectors.toList());
    }
}
