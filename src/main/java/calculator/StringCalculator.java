package calculator;

import io.micrometer.core.instrument.util.StringUtils;


public class StringCalculator {

    private static final int MIN_RESULT = 0;

    public int add(String argument) {
        if (StringUtils.isEmpty(argument)) {
            return MIN_RESULT;
        }

        Operands operands = OperandParser.extractOperands(argument);

        return operands.addAll();
    }
}
