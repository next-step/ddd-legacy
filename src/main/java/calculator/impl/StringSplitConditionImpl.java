package calculator.impl;

import calculator.SplitCondition;

public class StringSplitConditionImpl implements SplitCondition {

    @Override
    public String[] split(String value) {
        return value.split(",|:");
    }
}
