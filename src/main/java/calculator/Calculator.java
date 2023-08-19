package calculator;

import javax.transaction.NotSupportedException;

public class Calculator {

    private final CalculatorPolicies calculatorPolicies;

    public Calculator() {
        this.calculatorPolicies = new CalculatorPolicies();
    }

    public int calculate(String text) throws NotSupportedException {
        return calculatorPolicies.calculate(text);
    }
}
