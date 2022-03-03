package stringcalculator.operator.pattern;

import stringcalculator.operator.Operator;

public interface OperatorPattern {

    boolean isPattern(String input) ;

    Operator getOperator(String input);

}
