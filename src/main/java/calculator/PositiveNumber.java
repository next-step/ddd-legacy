package calculator;

import calculator.exception.NotPositiveNumberException;
import java.util.Objects;

public class PositiveNumber {

    private int number;

    public PositiveNumber(int number) {
        validate(number);
        this.number = number;
    }

    private void validate(int number) {
        if(!NumberUtils.isPositiveNumber(number)){
            throw new NotPositiveNumberException(String.format("number:%d is not positive ",number));
        }
    }

    public int getPrimitiveNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositiveNumber that = (PositiveNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
