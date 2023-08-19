package calculator.vo;

import calculator.SplitCondition;

public class Sum {
    private final SplitCondition condition;

    public Sum(SplitCondition condition) {
        this.condition = condition;
    }

    public int run(String value) {
        return new Integers(this.condition.split(value)).getIntegers().stream()
                .reduce(0, Integer::sum);
    }
}
