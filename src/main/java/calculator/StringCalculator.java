package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    String defaultPattern = ",|:";
    String delimiterPattern = "//(.*)\n(.*)";

    public int calculate(String input) {
        List<String> inputs = separateInputs(input);
        System.out.println(inputs);
        return getSum(parseInt(inputs));
    }

    public List<String> separateInputs(String input) {
        Matcher delimiterMatcher = Pattern.compile(delimiterPattern).matcher(input);
        String delimiter = defaultPattern;
        if (delimiterMatcher.find()) {
            input = delimiterMatcher.group(2);
            delimiter = defaultPattern + "|" + Pattern.quote(delimiterMatcher.group(1));
        }

        return Arrays.stream(input.trim()
                .split(delimiter))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public List<Integer> parseInt(List<String> asList) {
        return asList.stream()
                .map(this::parsePositiveInt)
                .collect(Collectors.toList());
    }

    private Integer parsePositiveInt(String string) {
        Matcher isNumeric = Pattern.compile("[+]?\\d+", Pattern.UNIX_LINES).matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }

    private int getSum(List<Integer> numbers) {
        return numbers.stream().reduce(Integer::sum).orElse(0);
    }
}