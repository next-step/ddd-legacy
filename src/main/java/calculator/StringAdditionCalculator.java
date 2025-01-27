package calculator;

import calculator.exception.InvalidNumberFormatException;
import calculator.exception.NegativeNumberException;

/**
 * 입력된 문자열을 처리하고 결과를 반환하는 역할
 */
public class StringAdditionCalculator {

    private final String text;

    public StringAdditionCalculator(final String text) {
        this.text = text;
    }

    public int add() {
        if (text == null || text.isEmpty()) {
            return 0;

        }

        String[] numbers = StringSplitter.split(text);
        int sum = 0;

        for (String number : numbers) {

            PositiveNumber positiveNumber = new PositiveNumber(number);
            sum += positiveNumber.getValue();
        }

        return sum;
    }
}

