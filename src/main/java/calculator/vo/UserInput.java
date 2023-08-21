package calculator.vo;

public class UserInput {

    private final String customOperator;
    private final String expression;

    public UserInput(String customOperator, String expression) {
        this.customOperator = customOperator;
        this.expression = expression;
    }

    public static UserInput of(String customOperator, String expression) {
        return new UserInput(customOperator, expression);
    }

    public String toExpression() {
        if (customOperator != null) {
            return expression.replace(customOperator, OperatorToken.DEFAULT_OPERATOR_TOKEN);
        }
        return expression;
    }
}
