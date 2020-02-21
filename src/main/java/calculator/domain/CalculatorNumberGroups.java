package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public class CalculatorNumberGroups {
    final List<CalculatorNumber> calculatorNumbers;

    public CalculatorNumberGroups(List<CalculatorNumber> calculatorNumbers) {
        this.calculatorNumbers = calculatorNumbers;
    }

    public int addAllCalculatorNumber() {
        return calculatorNumbers.stream()
                .collect(Collectors.summingInt(CalculatorNumber::getNumber));
    }
}
