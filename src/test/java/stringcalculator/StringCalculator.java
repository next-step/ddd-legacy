package stringcalculator;

public class StringCalculator {
    public int add(String rawStr) {
        if (rawStr == null || rawStr.isEmpty()) {
            return 0;
        }

        CalculatorFactory calculatorFactory = new CalculatorFactory(rawStr);

        StringExpression expression = calculatorFactory.buildExpression();
        StringDelimiters delimiters = calculatorFactory.buildDelimiters();

        return expression.calculateSum(delimiters);
    }
}
