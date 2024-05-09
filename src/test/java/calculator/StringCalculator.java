package calculator;

public class StringCalculator {

    public static final int RESULT_FOR_EMPTY_EXPRESSION = 0;

    private final String expression;

    public StringCalculator(String expression) {
        this.expression = expression;
    }

    public int getResult() {
        if (expression == null || expression.isEmpty()) {
            return RESULT_FOR_EMPTY_EXPRESSION;
        }
        return new Tokens(expression).sum();
    }
}
