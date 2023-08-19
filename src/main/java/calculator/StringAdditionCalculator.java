package calculator;

import calculator.factory.DividerFactory;
import calculator.vo.Sum;

public class StringAdditionCalculator {
    public StringAdditionCalculator() {
    }

    public int add(String value) {
        if (isEmpty(value)) return 0;
        DivideCondition divideCondition = new DividerFactory().getDivideCondition(value);
        return new Sum(divideCondition).run(value);
    }

    public boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
