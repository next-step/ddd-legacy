package calculator;

import calculator.exception.NegativeNumberException;

public class PositiveNumber {

    private static final int MINIMUM_POSITIVE_NUMBER = 0;
    private static final String EXCEPTION_MESSAGE_NEGATIVE_NUMBER = "문자열 계산기의 입력값은 음수가 될수 없습니다.";
    private int number;

    public PositiveNumber() {
        this(0);
    }

    public PositiveNumber(String numberText) {
        this(Integer.parseInt(numberText));
    }

    public PositiveNumber(int number) {
        validation(number);
        this.number = number;
    }

    private void validation(int number) {
        if (number < MINIMUM_POSITIVE_NUMBER) {
            throw new NegativeNumberException(EXCEPTION_MESSAGE_NEGATIVE_NUMBER);
        }
    }

    public int getNumber() {
        return number;
    }

    public PositiveNumber add(PositiveNumber positiveNumber) {
        return new PositiveNumber(this.number += positiveNumber.number);
    }
}
