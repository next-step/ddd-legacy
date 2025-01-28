package stringcalculator;

public class Calculator {

    private static final int DEFAULT_RESULT = 0;
    private static final String MINUS_OPERAND = "-";

    public static int calculate(final String operand) {
        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }
        validateNegativeNumber(operand);
        return CalculatorNumbers.createNumbers(operand).sum();
    }

    private static void validateNegativeNumber(final String operand) {
        if (operand.contains(MINUS_OPERAND)) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
