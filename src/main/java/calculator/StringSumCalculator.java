package calculator;

import calculator.impl.CustomDivideConditionImpl;
import calculator.impl.StringDivideConditionImpl;

public class StringSumCalculator {
    public StringSumCalculator() {
    }

    public int add(String value) {
        if (isEmpty(value)) return 0;

        String[] divide = new CustomDivideConditionImpl().divide(value);
        if (divide.length > 0) {
            return add(value, new CustomDivideConditionImpl());
        }
        return add(value, new StringDivideConditionImpl());
    }

    private int add(String value, DivideCondition condition) {
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
