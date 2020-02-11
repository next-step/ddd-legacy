package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final String DEFAULT_PATTERN = ",|:";
    private static final String DELIMITER_PATTERN = "//(.*)\n(.*)";
    private static final String NUMERIC_PATTERN = "[+]?\\d+";
    private static final String PATTERN_APPEND_CONNECTION = "|";

    public int calculate(String input) {
        List<String> inputs = splitInputs(input);
        return sum(parseInt(inputs));
    }

    public List<String> splitInputs(String input) {
        Matcher delimiterMatcher = Pattern.compile(DELIMITER_PATTERN).matcher(input);
        String delimiter = DEFAULT_PATTERN;
        if (delimiterMatcher.find()) {
            input = delimiterMatcher.group(2);
            delimiter = DEFAULT_PATTERN + PATTERN_APPEND_CONNECTION + Pattern.quote(delimiterMatcher.group(1));
        }
        return Arrays.stream(input.trim()
                .split(delimiter))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Integer> parseInt(List<String> asList) {
        return asList.stream()
                .map(this::parsePositiveInt)
                .collect(Collectors.toList());
    }

    private Integer parsePositiveInt(String string) {
        Matcher isNumeric = Pattern.compile(NUMERIC_PATTERN).matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().reduce(Integer::sum).orElse(0);
    }
}