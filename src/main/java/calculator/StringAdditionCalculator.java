package calculator;

import calculator.factory.SplitFactory;
import calculator.vo.Sum;

public class StringAdditionCalculator {
    public StringAdditionCalculator() {
    }

    public int add(String value) {
        if (isEmpty(value)) return 0;
        SplitCondition splitCondition = new SplitFactory().getSplitCondition(value);
        return new Sum(splitCondition).run(value);
    }

    public boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
