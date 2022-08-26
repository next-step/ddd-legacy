package stringcalculator;

import java.util.Arrays;

public class StringExpression {
    public static final int NUMBER_MINIMUM = 0;
    private final String expression;

    public StringExpression(String expression) {
        this.expression = expression;
    }

    public int calculateSum(StringDelimiters delimiters) {
        return Arrays.stream(expression.split(delimiters.asRegex())).mapToInt(Integer::parseInt).peek(num -> {
            if (num < NUMBER_MINIMUM) {
                throw new RuntimeException("음수는 계산할 수 없습니다");
            }
        }).sum();
    }
}
