package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    String defaultPattern = ",|:";
    String delimiterPattern = "//(.)\n(.*)";

    public List<String> separateInputs(String input) {
        return Arrays.stream(input.trim()
                .split(defaultPattern))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public String findDelimiter(String input) {
        Matcher delimiterMatcher = Pattern.compile(delimiterPattern).matcher(input);
        if (delimiterMatcher.find()) {
            return defaultPattern + "|" + delimiterMatcher.group(1);
        }
        return defaultPattern;
    }

    public List<Integer> parseInt(List<String> asList) {
        return asList.stream()
                .map(this::parsePositiveInt)
                .collect(Collectors.toList());
    }

    private Integer parsePositiveInt(String string) {
        Matcher isNumeric = Pattern.compile("[+]?\\d+").matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }
}