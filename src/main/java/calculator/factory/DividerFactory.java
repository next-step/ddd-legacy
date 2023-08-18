package calculator.factory;

import calculator.DivideCondition;
import calculator.impl.CustomDivideConditionImpl;
import calculator.impl.StringDivideConditionImpl;

public class DividerFactory {
    public DivideCondition getDivideCondition(String value) {
        if (new CustomDivideConditionImpl().supports(value)) {
            return new CustomDivideConditionImpl();
        }
        return new StringDivideConditionImpl();
    }
}
