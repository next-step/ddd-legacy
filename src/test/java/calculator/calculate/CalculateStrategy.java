package calculator.calculate;

import calculator.number.Number;

public interface CalculateStrategy {

    Number calculate(Number first, Number second);
}
