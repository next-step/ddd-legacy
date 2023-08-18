package calculator;

import calculator.factory.DividerFactory;

public class StringAdditionCalculator {
    public StringAdditionCalculator() {
    }

    public int add(String value) {
        if (isEmpty(value)) return 0;
        DivideCondition divideCondition = new DividerFactory().getDivideCondition(value);
        return sumToCondition(value, divideCondition);
    }

    private int sumToCondition(String value, DivideCondition condition) {
        int result = 0;
        for (String s : condition.divide(value)) {
            if (isEmpty(s)) continue;
            if (Integer.parseInt(s) < 0) throw new RuntimeException("음수는 입력할 수 없습니다.");

            result += Integer.parseInt(s);
        }
        return result;
    }


    public boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
