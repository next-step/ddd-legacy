package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return calculate(tokens);
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

    private int calculate(final String[] tokens) {
        try {
            return this.getSumOfEachToken(tokens);
        } catch (NumberFormatException nfe) {
            throw nfe;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private int getSumOfEachToken(final String[] tokens) {
        final List<Integer> list = new PositiveIntegers(tokens).getList();
        return getResultUsingReduce(list);
    }

    private Integer getResultUsingReduce(final List<Integer> list) {
        return list.stream()
                .reduce(0, Integer::sum);
    }
}
