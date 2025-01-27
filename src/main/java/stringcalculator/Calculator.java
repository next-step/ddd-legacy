package stringcalculator;

import java.util.regex.Pattern;

public class Calculator {

    private static final int DEFAULT_RESULT = 0;
    private static final String CUSTOM_REGEX = "//.\\n";

    public static int calculate(final String operand) {
        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }
        validateNegativeNumber(operand);
        return new CalculatorNumbers(operand).sum();
    }

    private static void validateNegativeNumber(final String operand) {
        boolean isCustomOperator = Pattern.compile(CUSTOM_REGEX).matcher(operand).find();
        if (!isCustomOperator && operand.contains("-")) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
