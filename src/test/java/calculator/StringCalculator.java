package calculator;

import calculator.calculate.AddCalculateStrategy;
import calculator.number.PositiveNumber;
import calculator.number.PositiveNumbers;

public class StringCalculator {

    public int add(final String text) {
        if (isNullOrEmpty(text)) {
            return PositiveNumber.ZERO.getNumber();
        }

        return PositiveNumbers.of(text)
                .calculate(new AddCalculateStrategy())
                .getNumber();
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }
}
