package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculatorParser {

    private final Pattern defaultDelimiterPattern = Pattern.compile("[,:]");
    private final Pattern customDelimiterPattern = Pattern.compile("//(.)\n(.*)");

    public Integers parse(final String input) {
        List<Integer> integers =
                Arrays.stream(splitByDelimiters(input)).map(Integer::parseInt).collect(Collectors.toList());

        return new Integers(integers);
    }

    private String[] splitByDelimiters(final String input) {
        final Matcher customDelimiterMatcher = customDelimiterPattern.matcher(input);
        if (customDelimiterMatcher.find()) {
            final String customDelimiter = customDelimiterMatcher.group(1);
            return customDelimiterMatcher.group(2).split(customDelimiter);
        }

        return input.split(defaultDelimiterPattern.pattern());
    }
}
