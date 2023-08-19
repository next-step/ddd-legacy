package calculator.vo;

import calculator.DivideCondition;

public class Sum {
    private final DivideCondition condition;

    public Sum(DivideCondition condition) {
        this.condition = condition;
    }

    public int run(String value) {
        return new Integers(this.condition.divide(value)).getIntegers().stream()
                .reduce(0, Integer::sum);
    }
}
