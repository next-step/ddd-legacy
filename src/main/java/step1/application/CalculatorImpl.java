package step1.application;

import org.springframework.util.StringUtils;
import step1.common.CalculatorConstant;
import step1.domain.Number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorImpl implements Calculator {

    private final Matcher matcher;

    public CalculatorImpl(String expression) {
        if (expression == null) {
            expression = CalculatorConstant.EMPTY;
        }

        matcher = Pattern.compile(CalculatorConstant.CUSTOM_OPERATOR_PATTERN)
                .matcher(expression);
    }

    @Override
    public ArrayList<String> parseOperators(String expression) {
        if (matcher.find()) {
            return new ArrayList<>(Collections.singletonList(matcher.group(1)));
        }

        return new ArrayList<>(Arrays.asList(CalculatorConstant.COMMA, CalculatorConstant.COLON));
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

    private void collectNumbers(String expression, List<step1.domain.Number> numbers, ArrayList<String> operators) {
        if (operators.size() == 1) {
            this.collectNumbersByCustomOperator(numbers);
            return;
        }

        String splitExpression = getSplitExpression(operators);
        for (String value : expression.split(splitExpression)) {
            numbers.add(new Number(value));
        }
    }

    private String getSplitExpression(ArrayList<String> operators) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String operator : operators) {
            this.addOperator(operator, stringBuilder);
        }

        return stringBuilder.toString();
    }

    private void addOperator(String operator, StringBuilder stringBuilder) {
        if (stringBuilder.toString().equals(CalculatorConstant.EMPTY)) {
            stringBuilder.append(operator);
            return;
        }

        stringBuilder.append(CalculatorConstant.OR).append(operator);
    }

    private void collectNumbersByCustomOperator(List<Number> numbers) {
        String customOperator = matcher.group(1);
        String expression = matcher.group(2);

        for (String value : expression.split(customOperator)) {
            numbers.add(new Number(value));
        }
    }

}
