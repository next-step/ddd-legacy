package string_additional_calculator;

class StringAdditionalCalculator {
    private final ExpressionSeparator expressionSeparator;

    public StringAdditionalCalculator(ExpressionSeparator expressionSeparator) {
        this.expressionSeparator = expressionSeparator;
    }

    public int calculate(String expression) {
        String[] stringNumbers = expressionSeparator.separate(expression);
        Constant result = Constant.ZERO;
        for (String stringNumber : stringNumbers) {
            Constant constant = Constant.from(stringNumber);
            result = result.sum(constant);
        }
        return result.getValue();
    }
}