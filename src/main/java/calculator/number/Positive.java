package calculator.number;

import calculator.exception.CalculatorException;


import java.util.Objects;

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

    public int getValue() {
        return this.value;
    }

    public Positive add(Positive addend) {
        return new Positive(this.value + addend.getValue());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Positive positive = (Positive) object;
        return value == positive.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
