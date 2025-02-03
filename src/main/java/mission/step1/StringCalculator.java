package mission.step1;

import org.springframework.util.StringUtils;

public class StringCalculator {
    private static final int ZERO_VALUE = 0;
    private final CompositeParser parser;
    private final Calculator calculator;

    public StringCalculator(CompositeParser parser) {
        this.parser = parser;
        this.calculator = new Calculator();
    }

    public int add(String expression) {
        if (!StringUtils.hasText(expression)) {
            return ZERO_VALUE;
        }

        String[] numbers = parser.splitWithDelimiter(expression);
        return calculateSum(numbers);
    }

    private int calculateSum(String[] numbers) {
        PositiveNumber accumulated = PositiveNumber.from("0");
        for (String number : numbers) {
            accumulated = calculator.calculate(accumulated, PositiveNumber.from(number));
        }
        return accumulated.getValue();
    }
}
