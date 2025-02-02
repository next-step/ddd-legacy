package mission.step1;

import org.springframework.util.StringUtils;

public class StringCalculator {

    private static final int ZERO_VALUE = 0;

    private final CompositeParser parser;
    private final Calculable calculator;

    public StringCalculator(CompositeParser parser, Calculable calculator) {
        this.parser = parser;
        this.calculator = calculator;
    }

    public int add(String expression) {
        if (!StringUtils.hasText(expression)) {
            return ZERO_VALUE;
        }

        String[] numbers = parser.splitWithDelimiter(expression);
        return calculateSum(numbers);
    }

    private int calculateSum(String[] numbers) {
        for (String number : numbers) {
            calculator.calculate(PositiveNumber.from(number));
        }
        return calculator.getResult();
    }
}
