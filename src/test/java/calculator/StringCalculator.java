package calculator;

public class StringCalculator {

    public static final int RESULT_FOR_EMPTY_EXPRESSION = 0;

    public int calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return RESULT_FOR_EMPTY_EXPRESSION;
        }
        return Tokens.from(expression).sum();
    }
}
