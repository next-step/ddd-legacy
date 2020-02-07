package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public class Calculator {

    private static final String ZERO = "0";

    private String expression;

    public Calculator(String expression) {
        this.expression = validateByNullOrEmpty(expression);
    }

    private String validateByNullOrEmpty(String expression) {
        if (Strings.isBlank(expression)) {
            return ZERO;
        }
        return expression;
    }

    public String getExpression() {
        return expression;
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
