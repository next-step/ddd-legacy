package calculator.calculate;

import calculator.number.Number;

public class AddCalculateStrategy implements CalculateStrategy {

    @Override
    public Number calculate(final Number first, final Number second) {
        return first.sum(second);
    }
}
