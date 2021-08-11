package step1.application;

import org.springframework.util.StringUtils;
import step1.domain.Number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator implements Calculator {

    public static String CUSTOM_OPERATOR_PATTERN = "//(.)\n(.*)";
    public static String COMMA = ",";
    public static String COLON = ":";
    public static String EMPTY = "";
    public static String OR = "|";

    private final Matcher matcher;

    public StringCalculator(String expression) {
        if (expression == null) {
            expression = EMPTY;
        }

        matcher = Pattern.compile(CUSTOM_OPERATOR_PATTERN)
                .matcher(expression);
    }

    @Override
    public List<String> parseOperators(String expression) {
        if (matcher.find()) {
            return new ArrayList<>(Collections.singletonList(matcher.group(1)));
        }

        return new ArrayList<>(Arrays.asList(COMMA, COLON));
    }

    @Override
    public List<step1.domain.Number> parseNumbers(String expression) {
        List<step1.domain.Number> numbers = new ArrayList<>();
        this.collectNumbers(expression, numbers, this.parseOperators(expression));
        return numbers;
    }

    @Override
    public int sum(String expression) {
        if (!StringUtils.hasLength(expression)) {
            return 0;
        }

        int result = 0;
        for (Number value : this.parseNumbers(expression)) {
            result += value.getValue();
        }

        return result;
    }

    private void collectNumbers(String expression, List<step1.domain.Number> numbers, List<String> operators) {
        if (operators.size() == 1) {
            this.collectNumbersByCustomOperator(numbers);
            return;
        }

        String splitExpression = getSplitExpression(operators);
        for (String value : expression.split(splitExpression)) {
            numbers.add(new Number(value));
        }
    }

    private String getSplitExpression(List<String> operators) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String operator : operators) {
            this.addOperator(operator, stringBuilder);
        }

        return stringBuilder.toString();
    }

    private void addOperator(String operator, StringBuilder stringBuilder) {
        if (stringBuilder.toString().equals(EMPTY)) {
            stringBuilder.append(operator);
            return;
        }

        stringBuilder.append(OR).append(operator);
    }

    private void collectNumbersByCustomOperator(List<Number> numbers) {
        String customOperator = matcher.group(1);
        String expression = matcher.group(2);

        for (String value : expression.split(customOperator)) {
            numbers.add(new Number(value));
        }
    }

}
