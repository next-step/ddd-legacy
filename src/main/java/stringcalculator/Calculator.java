package stringcalculator;

import java.util.regex.Pattern;

public class Calculator {

    private static final int DEFAULT_RESULT = 0;
    private static final String CUSTOM_DELIMETER = "//.\\n";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMETER);
    public static final String MINUS_OPERAND = "-";

    public static int calculate(final String operand) {
        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }
        validateNegativeNumber(operand);
        return new CalculatorNumbers(operand).sum();
    }

    private static void validateNegativeNumber(final String operand) {
        final boolean isCustomOperator = CUSTOM_PATTERN.matcher(operand).find();
        if (!isCustomOperator && operand.contains(MINUS_OPERAND)) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
