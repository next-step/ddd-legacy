package calculator.number;

import calculator.calculate.CalculateStrategy;

@FunctionalInterface
public interface Numbers {

    Number calculate(CalculateStrategy strategy);
}
