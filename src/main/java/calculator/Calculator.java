package calculator;

import calculator.domain.Formula;

public class Calculator {
    private static final int ZERO = 0;


    public int add(String value) {
        if (isNullAndBlank(value)) {
            return ZERO;
        }

        Formula formula = new Formula(value);

        return formula.sum();
    }

    private boolean isNullAndBlank(String value) {
        return value == null || value.isBlank();
    }
}
