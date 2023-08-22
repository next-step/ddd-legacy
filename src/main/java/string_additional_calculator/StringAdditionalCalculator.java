package string_additional_calculator;

import java.util.List;

class StringAdditionalCalculator {
    private final ExpressionSeparator expressionSeparator;

    public StringAdditionalCalculator(ExpressionSeparator expressionSeparator) {
        this.expressionSeparator = expressionSeparator;
    }

    public PositiveNumber calculate(String expression) {
        if (expression == null || expression.isEmpty()) {
            return PositiveNumber.ZERO;
        }
        PositiveNumbers positiveNumbers = PositiveNumbers.of(List.of(expressionSeparator.separate(expression)));
        return positiveNumbers.totalSum();
    }
}
