package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public class Calculator {

    private static final int ZERO = 0;
    private static final int MIN_EXPRESSION_SIZE = 1;

    private String expression;
    private int sum;

    public Calculator(String expression) {
        this.expression = expression;
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

    public int operate() {
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
