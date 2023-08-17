package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculatorParser {

    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,:]");
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public PositiveIntegers parse(final String input) {
        List<PositiveInteger> integers = Arrays.stream(splitByDelimiters(input)).map(Integer::parseInt)
                .map(PositiveInteger::new)
                .collect(Collectors.toList());

        return new PositiveIntegers(integers);
    }

    private String[] splitByDelimiters(final String input) {
        final Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (customDelimiterMatcher.find()) {
            final String customDelimiter = customDelimiterMatcher.group(1);
            return customDelimiterMatcher.group(2).split(customDelimiter);
        }

        return input.split(DEFAULT_DELIMITER_PATTERN.pattern());
    }
}
