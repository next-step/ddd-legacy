package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculatorParser {
    private static final String DELIMITERS = "[,:]";
    private static final String CUSTOM_DELIMITER_REGEX = "(?<=//)(.*)(?=\n)";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER_REGEX);
    private static final String EXPRESSION_SEPARATOR = "\n";

    public List<Integer> execute(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression should not be null or empty in StringCalculatorParser");
        }
        List<String> customDelimiters = getCustomDelimiters(expression);
        if (customDelimiters.size() > 0) {
            String[] seperatedExpressions = expression.split(EXPRESSION_SEPARATOR);
            String mathExpression = seperatedExpressions[seperatedExpressions.length - 1];
            return getNumbers(mathExpression, getCustomDelimiterString(customDelimiters));
        }
        return getNumbers(expression, DELIMITERS);
    }

    private List<String> getCustomDelimiters(String expression) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(expression);
        List<String> customDelimiters = new ArrayList<>();
        while (matcher.find()) {
            customDelimiters.add(matcher.group());
        }
        return customDelimiters;
    }

    private String getCustomDelimiterString(List<String> customDelimiters) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        customDelimiters.forEach(sb::append);
        sb.append("]");
        return sb.toString();
    }

    private List<Integer> getNumbers(String expression, String delimiters) {
        String[] exprs = expression.split(delimiters);
        try {
            return Arrays.stream(exprs).map(Integer::parseInt).toList();
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자형식이 잘못되었습니다");
        }
    }
}
