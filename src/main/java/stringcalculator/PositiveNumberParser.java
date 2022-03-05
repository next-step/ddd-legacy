package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PositiveNumberParser {

    private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(",", ":");
    private static final String CUSTOM_DELIMITER_PREFIX = "//";
    private static final String NEW_LINE = "\n";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_PREFIX + "(.)");

    public PositiveNumbers parse(String text) {
        if (isNullOrEmpty(text)) {
            return new PositiveNumbers();
        }

        final String numberText = parseNumberText(text);
        final String allDelimiterRegex = findAllDelimiterRegex(text);

        return new PositiveNumbers(
            Arrays.stream(numberText.split(allDelimiterRegex))
                .map(Integer::parseInt)
                .map(PositiveNumber::new)
                .collect(Collectors.toList())
        );
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private String parseNumberText(String text) {
        final String[] splitText = text.split(NEW_LINE);
        return splitText[splitText.length - 1];
    }

    private String findAllDelimiterRegex(String text) {
        return findAllDelimiters(text).stream()
            .collect(Collectors.joining("|", "[", "]"));
    }

    private List<String> findAllDelimiters(String text) {
        final List<String> customDelimiters = findCustomDelimiters(text);

        return Stream.concat(DEFAULT_DELIMITERS.stream(), customDelimiters.stream())
            .collect(Collectors.toList());
    }

    private List<String> findCustomDelimiters(String text) {
        return Arrays.stream(text.split(NEW_LINE))
            .filter(it -> CUSTOM_DELIMITER_PATTERN.matcher(it).matches())
            .map(it -> it.replaceAll(CUSTOM_DELIMITER_PREFIX, ""))
            .collect(Collectors.toList());
    }
}
