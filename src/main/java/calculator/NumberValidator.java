package calculator;


import calculator.exception.NotPositiveNumberException;

public class NumberValidator {

    public void validatePositiveNumber(int value) {
        boolean isPositiveNumber = value >= 0;
        if(!isPositiveNumber){
            throw new NotPositiveNumberException("value is " + value);
        }
    }
}
