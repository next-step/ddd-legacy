package stringcalculator;

public class StringCalculator {
    public int add(String calcInput) {
        if (calcInput == null || calcInput.isBlank()) {
            return 0;
        }

        CalculatorInput calculatorInput = new CalculatorInput(calcInput);
        Operands operands = new Operands(calculatorInput.parse());
        return operands.sum();
    }
}
