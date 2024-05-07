package calculator;

public class StringCalculator {

    public int calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }
        return Tokens.from(expression).sum();
    }
}
