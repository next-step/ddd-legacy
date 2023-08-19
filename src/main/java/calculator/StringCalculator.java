package calculator;

import io.micrometer.core.instrument.util.StringUtils;

public class StringCalculator {

    public final OperandParser operandParser;

    public StringCalculator(OperandParser operandParser) {
        this.operandParser = operandParser;
    }

    private static final int MIN_RESULT = 0;

    public int add(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return MIN_RESULT;
        }

        Operands operands = operandParser.extractOperands(tokenValue);
        return operands.addAll();
    }
}
