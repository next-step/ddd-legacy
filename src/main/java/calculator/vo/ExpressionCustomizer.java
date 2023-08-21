package calculator.vo;

public class ExpressionCustomizer {

    private final String customOperator;
    private final String expression;

    public ExpressionCustomizer(String customOperator, String expression) {
        this.customOperator = customOperator;
        this.expression = expression;
    }

    public static ExpressionCustomizer of(String customOperator, String expression) {
        return new ExpressionCustomizer(customOperator, expression);
    }

    public String toExpression() {
        if (customOperator != null) {
            return expression.replace(customOperator, OperatorToken.DEFAULT_OPERATOR_TOKEN);
        }
        return expression;
    }
}
