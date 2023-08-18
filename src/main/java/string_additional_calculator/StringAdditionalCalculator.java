package string_additional_calculator;

import java.util.List;

class StringAdditionalCalculator {
    private final ExpressionSeparator expressionSeparator;

    public StringAdditionalCalculator(ExpressionSeparator expressionSeparator) {
        this.expressionSeparator = expressionSeparator;
    }

    public int calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }
        PositiveNumbers positiveNumbers = PositiveNumbers.of(List.of(expressionSeparator.separate(expression)));
        return positiveNumbers.totalSum().getValue();
    }
}
