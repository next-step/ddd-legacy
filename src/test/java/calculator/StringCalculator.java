package calculator;

import java.util.Arrays;

public class StringCalculator {

    public static final String DEFAULT_DELIMITER = ",|:";
    public static final String PREFIX_OF_CUSTOM_DELIMITER = "//";
    public static final String SUFFIX_OF_CUSTOM_DELIMITER = "\n";

    public int calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }
        return sum(toNumbers(split(expression)));
    }

    private int sum(int[] numbers) {
        return Arrays.stream(numbers).sum();
    }

    private int[] toNumbers(String[] strNumbers) {
        return Arrays.stream(strNumbers)
            .map(this::toPositive)
            .mapToInt(Integer::intValue)
            .toArray();
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

    private int toPositive(String strNumber) {
        int number = parseInt(strNumber);
        if (number < 0) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
        return number;
    }

    private int parseInt(String strNumber) {
        try {
            return Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값은 입력할 수 없습니다.");
        }
    }
}
