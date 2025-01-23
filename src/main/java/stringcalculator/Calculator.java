package stringcalculator;

import java.util.regex.Pattern;

public class Calculator {

    public static final String DEFAULT_RESULT = "0";
    public static final String CUSTOM_REGEX = "//.\\n";

    public static String calculate(final String operand) {
        if (operand == null || operand.isEmpty()) {
            return DEFAULT_RESULT;
        }
        validateNegativeNumber(operand);
        return operand;
    }

    private static void validateNegativeNumber(String operand) {
        boolean isCustomOperator = Pattern.compile(CUSTOM_REGEX).matcher(operand).find();
        if (!isCustomOperator && operand.contains("-")) {
            throw new RuntimeException("음수는 입력할 수 없습니다");
        }
    }

}
