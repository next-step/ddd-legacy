package stringcalculator;

public class StringCalculator {
    public int add(String calculatorInputString) {
        if (calculatorInputString == null || calculatorInputString.isBlank()) {
            return 0;
        }

        CalculatorInput calculatorInput = new CalculatorInput(calculatorInputString);
        Operands operands = new Operands(calculatorInput.parse());
        return operands.sum();
    }
}
