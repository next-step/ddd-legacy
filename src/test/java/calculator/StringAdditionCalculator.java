package calculator;

import java.util.Arrays;

public class StringAdditionCalculator {
    public Integer calculate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        StringAdditionCalculatorDelimiterParser delimiterParser = new StringAdditionCalculatorDelimiterParser(text);
        return Arrays.stream(delimiterParser.getNumbers().split("[" + delimiterParser.getDelimiters() + "]"))
            .mapToInt(this::parseInt)
            .peek(this::checkNegative)
            .sum();
    }

    private Integer parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new RuntimeException();
        }
    }

    private void checkNegative(Integer number) {
        if (number < 0) {
            throw new RuntimeException();
        }
    }
}
