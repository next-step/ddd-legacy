package stringcalculator;

public class StringCalculator {
    public int add(String calcInput) {
        if (calcInput == null || calcInput.isBlank()) {
            return 0;
        }

        CalculatorInput calculatorInput = new CalculatorInput(calcInput);
        String[] parsedInput = calculatorInput.parse();
        Operands operands = new Operands(parsedInput);
        return operands.sum();
    }

    public static void main(String[] args) {
        StringCalculator calculator = new StringCalculator();
        System.out.println(calculator.add("//$\n1,2$3"));
    }
}
