package calculator;

import calculator.exception.InvalidNumberFormatException;
import calculator.exception.NegativeNumberException;

public class StringCalculator {

    private String text;

    public StringCalculator(final String text) {
        this.text = text;
    }

    public int add() {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] numbers = Delimiter.split(text);
        int sum = 0;

        for (String number : numbers) {
            if (!isNumeric(number)) {
                throw new InvalidNumberFormatException("Invalid input: Non-numeric value found: " + number);
            }

            int parsedNumber = Integer.parseInt(number);

            if (parsedNumber < 0) {
                throw new NegativeNumberException("Negative numbers are not allowed: " + number);
            }
            sum += parsedNumber;
        }

        return sum;
    }

    private boolean isNumeric(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
