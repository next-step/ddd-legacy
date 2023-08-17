package stringcalculator;

public class StringCalculator {
    public int add(String calcInput) {
        if (calcInput == null || calcInput.isBlank()) {
            return 0;
        }

        CalculatorInput calculatorInput = new CalculatorInput(calcInput);
        String[] parsedInput = calculatorInput.parse();
        return 1;
    }
}
