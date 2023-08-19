package calculator.factory;

import calculator.SplitCondition;
import calculator.impl.CustomSplitConditionImpl;
import calculator.impl.StringSplitConditionImpl;

public class SplitFactory {
    public SplitCondition getSplitCondition(String value) {
        if (new CustomSplitConditionImpl().supports(value)) {
            return new CustomSplitConditionImpl();
        }
        return new StringSplitConditionImpl();
    }
}
