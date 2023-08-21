package calculator.domain;

import calculator.exception.NegativeInputException;

public class PositiveNumber {

    private final int number;

    public PositiveNumber(final int number) {
        if(number < 0) {
            throw new NegativeInputException();
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public PositiveNumber sum(PositiveNumber number) {
        return new PositiveNumber(this.number + number.getNumber());
    }

}
