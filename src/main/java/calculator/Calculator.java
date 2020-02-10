package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private static final int ZERO = 0;
    private static final int MIN_EXPRESSION_SIZE = 1;
    private static final String SEPARATOR = ",|:";
    private static final String CUSTOM_SEPARATOR = "//(.)\n(.*)";

    private String expression;
    private int sum;
    private Pattern pattern;

    public Calculator(String expression) {
        this.expression = expression;
        this.pattern = Pattern.compile(CUSTOM_SEPARATOR);
        this.sum = validateByNullOrEmpty();
    }

    private int validateByNullOrEmpty() {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }
        return validateByExpressionSize();
    }

    private int validateByExpressionSize() {
        if (expression.length() == MIN_EXPRESSION_SIZE) {
            return Integer.parseInt(expression);
        }
        return operate();
    }

    private void validateByNegativeNumber(String number) {
        if (Integer.parseInt(number) < ZERO) {
            throw new RuntimeException("음수는 허용하지 않습니다.");
        }
    }

    private int operate() {
        String[] numbers = separateNumber();
        for (String number : numbers) {
            validateByNegativeNumber(number);
            sum += Integer.parseInt(number);
        }
        return sum;
    }

    private String[] separateNumber() {
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return expression.split(SEPARATOR);
    }

    public int getResult() {
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calculator that = (Calculator) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
