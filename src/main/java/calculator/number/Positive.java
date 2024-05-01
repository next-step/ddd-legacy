package calculator.number;

import calculator.exception.CalculatorException;

import static calculator.exception.ErrorMessage.IS_NOT_NEGATIVE;

public class Positive {

    private final int value;

    public static final Positive ZERO = new Positive(0);

    public Positive(String value) {
        this(Integer.parseInt(value));
    }

    public Positive(Integer value) {
        if (value < 0) {
            throw new CalculatorException(IS_NOT_NEGATIVE);
        }
        this.value = value;
    }

    public int getIntValue() {
        return this.value;
    }

}
