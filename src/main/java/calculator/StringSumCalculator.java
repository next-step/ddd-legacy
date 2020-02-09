package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringSumCalculator {

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = ",|:";

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
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            return "[" + matcher.group(1) + "]";
        }
        return DEFAULT_DELIMITER;
    }

    private String findRealExpression(final String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return text;
    }

    private int calc(final String[] tokens) {
        try {
            return this.loop(tokens);
        } catch (NumberFormatException nfe) {
            throw nfe;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
            throw new RuntimeException("입력값으로 음수가 사용됨");
        }
    }

    private List<Integer> getIntegers(final String[] tokens) {
        return Arrays.stream(tokens)
                    .map(token -> (token.isEmpty() ? 0 : Integer.parseInt(token)))
                    .collect(Collectors.toList());
    }
}
