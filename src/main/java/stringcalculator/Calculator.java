package stringcalculator;

import java.util.regex.Pattern;

public class Calculator {

    public static final int DEFAULT_RESULT = 0;
    public static final String CUSTOM_REGEX = "//.\\n";

    static CalculatorNumbers calculatorNumbers;

    public static int calculate(final String operand) {
        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }

        validateNegativeNumber(operand);

        if (operand.chars().allMatch(Character::isDigit)) {
            return Integer.parseInt(operand);
        }
        calculatorNumbers = new CalculatorNumbers(operand);
        return calculatorNumbers.sum();
    }

    private static void validateNegativeNumber(final String operand) {
        boolean isCustomOperator = Pattern.compile(CUSTOM_REGEX).matcher(operand).find();
        if (!isCustomOperator && operand.contains("-")) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
