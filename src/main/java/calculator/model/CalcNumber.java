package calculator.model;

import java.util.Objects;

public class CalcNumber {

    public static final CalcNumber DEFAULT_CALC_VALUE = new CalcNumber(0);
    public int value;

    public CalcNumber(int value) {
        this.validate(value);
        this.value = value;
    }

    public CalcNumber(String str) {
        this(Integer.parseInt(str));
    }

    public int getValue() {
        return this.value;
    }

    private void validate(int value) {
        Objects.requireNonNull(value);
        if (value < 0) {
            throw new RuntimeException("Number must be positive");
        }
    }

    public CalcNumber sum(CalcNumber num) {
        return new CalcNumber(this.getValue() + num.getValue());
    }
}
