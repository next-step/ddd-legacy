package calculator;

import javax.transaction.NotSupportedException;
import java.util.List;

public class CalculatorPolicies {

    List<CalculatorPolicy> calculatorPolicies;

    public CalculatorPolicies() {
        this.calculatorPolicies = List.of(
                new ZeroCalculatorPolicy(),
                new NumberCalculatorPolicy(),
                new SplitCalculatorPolicy(),
                new CustomSplitCalculatorPolicy()
        );
    }

    public int calculate(String text) throws NotSupportedException {
        return calculatorPolicies.stream()
                .filter(calculatorPolicy -> calculatorPolicy.isSupport(text))
                .map(calculatorPolicy -> calculatorPolicy.calculate(text))
                .findAny()
                .orElseThrow(NotSupportedException::new);
    }

}
