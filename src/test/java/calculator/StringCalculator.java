package calculator;

import java.util.Arrays;
import java.util.List;

public class StringCalculator {

    public static final String DEFAULT_DELIMITER = ",|:";
    public static final String PREFIX_OF_CUSTOM_DELIMITER = "//";
    public static final String SUFFIX_OF_CUSTOM_DELIMITER = "\n";

    public int calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }
        return sum(toPositiveIntegers(split(expression)));
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
    }

    private List<Integer> toPositiveIntegers(String[] strNumbers) {
        return Arrays.stream(strNumbers)
            .map(PositiveInteger::new)
            .map(PositiveInteger::value)
            .toList();
    }

    private String[] split(String expression) {
        String delimiter = extractDelimiter(expression);
        expression = removeDelimiter(expression);
        return expression.split(delimiter);
    }

    private String extractDelimiter(String expression) {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return DEFAULT_DELIMITER;
        }
        int endIndex = expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER);
        return expression.substring(PREFIX_OF_CUSTOM_DELIMITER.length(), endIndex);
    }

    private String removeDelimiter(String expression) {
        if (!expression.startsWith(PREFIX_OF_CUSTOM_DELIMITER)) {
            return expression;
        }
        return expression.substring(expression.indexOf(SUFFIX_OF_CUSTOM_DELIMITER) + 1);
    }
}
