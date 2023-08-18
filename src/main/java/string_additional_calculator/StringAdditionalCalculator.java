package string_additional_calculator;

class StringAdditionalCalculator {
    private final ExpressionSeparator expressionSeparator;

    public StringAdditionalCalculator(ExpressionSeparator expressionSeparator) {
        this.expressionSeparator = expressionSeparator;
    }

    public int calculate(String expression) {
        String[] stringNumbers = expressionSeparator.separate(expression);
        PositiveNumber result = PositiveNumber.ZERO;
        for (String stringNumber : stringNumbers) {
            PositiveNumber positiveNumber = PositiveNumber.from(stringNumber);
            result = result.sum(positiveNumber);
        }
        return result.getValue();
    }
}
