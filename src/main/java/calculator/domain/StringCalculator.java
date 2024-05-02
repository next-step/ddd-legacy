package calculator.domain;

import java.util.List;

public class StringCalculator {

    public int calculate(SplitStrategy strategy, String stringNumbers) {
        List<Number> splitNumbers = strategy.splitRefactoring(stringNumbers);
        Number result = new Number();

        for (Number number : splitNumbers) {
            result = result.add(number);
        }

        return result.getNumber();
    }
}
