package stringcalculator;

import stringcalculator.operator.OperatorSelector;

import static stringcalculator.Number.ZERO;

public class StringCalculator {

    private static final int SINGLE_STRING_LENGTH = 1;


    public int add(String source) {
        if (isNullOrEmpty(source)) {
            return ZERO.getValue();
        }
        if (isSingleLengthString(source)) {
            return new Number(source).getValue();
        }
        return OperatorSelector.selectOperator(source)
                .add();
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }

    private boolean isSingleLengthString(String input) {
        return input.length() == SINGLE_STRING_LENGTH;
    }
}
