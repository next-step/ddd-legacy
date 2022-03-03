package stringcalculator;

import stringcalculator.number.Number;
import stringcalculator.number.ZeroNumber;
import stringcalculator.operator.OperatorSelector;

public class StringCalculator {

    private static final int SINGLE_STRING_LENGTH = 1;

    private final String source;

    public StringCalculator(String source) {
        this.source = source;
    }

    public int add() {
        if (isNullOrEmpty(source)) {
            return new ZeroNumber().getValue();
        }

        if (isSingleLength(source)) {
            return new Number(source).getValue();
        }

        return new OperatorSelector().select(source).add();
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }

    private boolean isSingleLength(String input) {
        return input.length() == SINGLE_STRING_LENGTH;
    }

}
