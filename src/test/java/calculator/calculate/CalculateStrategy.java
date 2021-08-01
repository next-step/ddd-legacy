package calculator.calculate;

import calculator.Number;

public interface CalculateStrategy {

    Number calculate(Number first, Number second);
}
