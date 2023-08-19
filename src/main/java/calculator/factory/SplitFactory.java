package calculator.factory;

import calculator.SplitCondition;
import calculator.impl.CustomSplitConditionImpl;
import calculator.impl.StringSplitConditionImpl;

public class SplitFactory {
    private static final StringSplitConditionImpl STRING_SPLIT_CONDITION = new StringSplitConditionImpl();
    private static final CustomSplitConditionImpl CUSTOM_SPLIT_CONDITION = new CustomSplitConditionImpl();

    public SplitFactory() {
    }

    public SplitCondition getSplitCondition(String value) {
        if (CUSTOM_SPLIT_CONDITION.supports(value)) {
            return CUSTOM_SPLIT_CONDITION;
        }
        return STRING_SPLIT_CONDITION;
    }

    public boolean isSupports(String value) {
        return CUSTOM_SPLIT_CONDITION.supports(value);
    }
}
