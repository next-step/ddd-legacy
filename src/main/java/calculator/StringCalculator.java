package calculator;

import calculator.application.OperandParser;
import calculator.util.Validate;

public class StringCalculator {

    public int add(final String input) {
        if (Validate.isNullOrBlank(input)) {
            return 0;
        }

        return new OperandParser()
                .parser(input)
                .sum();
    }


}
