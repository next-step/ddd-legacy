package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringSumCalculator {

    private Pattern customDelimiterPattern = Pattern.compile("//(.)\n(.*)");

    public int sum(final String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        final String delimiter = findDelimiter(text);
        final String expression = findRealExpression(text);
        final String[] tokens = expression.split(delimiter, -1);
        return calc(tokens);
    }

    private String findDelimiter(final String text) {
        Matcher matcher = customDelimiterPattern.matcher(text);
        if (matcher.find()) {
            return "[" + matcher.group(1) + "]";
        }
        return ",|:";
    }

    private String findRealExpression(final String text) {
        Matcher matcher = customDelimiterPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return text;
    }

    private int calc(final String[] tokens) {
        try {
            return this.loop(tokens);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    private int loop(final String[] tokens) {
        final List<Integer> list = getIntegers(tokens);
        verifyHasNegativeNumber(list);
        return getResultUsingReduce(list);
    }

    private Integer getResultUsingReduce(final List<Integer> list) {
        return list.stream()
                .reduce(0, Integer::sum);
    }

    private void verifyHasNegativeNumber(final List<Integer> list) {
        final boolean hasNegative = list.stream().anyMatch(item -> item < 0);
        if (hasNegative) {
            throw new RuntimeException();
        }
    }

    private List<Integer> getIntegers(final String[] tokens) {
        return Arrays.stream(tokens)
                    .map(token -> (token.isEmpty() ? 0 : Integer.parseInt(token)))
                    .collect(Collectors.toList());
    }
}
