package stringcalculator;

import stringcalculator.operator.OperatorSelector;

import static stringcalculator.Number.ZERO;

public class StringCalculator {

    private static final int SINGLE_STRING_LENGTH = 1;

    private StringCalculator() {
    }

    public static int add(String source) {
        if (isNullOrEmpty(source)) {
            return ZERO.getValue();
        }
        if (isSingleLengthString(source)) {
            return new Number(source).getValue();
        }
        return OperatorSelector.selectOperator(source)
                .add();
    }

    private static boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }

    private static boolean isSingleLengthString(String input) {
        return input.length() == SINGLE_STRING_LENGTH;
    }
}
