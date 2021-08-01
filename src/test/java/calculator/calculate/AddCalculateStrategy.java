package calculator.calculate;

import calculator.Number;

public class AddCalculateStrategy implements CalculateStrategy {

    @Override
    public Number calculate(Number first, Number second) {
        return first.sum(second);
    }
}
