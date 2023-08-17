package calculator.impl;

import calculator.DivideCondition;

public class StringDivideConditionImpl implements DivideCondition {
    @Override
    public String[] divide(String value) {
        return value.split(",|:");
    }
}
