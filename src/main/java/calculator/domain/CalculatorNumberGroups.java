package calculator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CalculatorNumberGroups {
    private final List<CalculatorNumber> calculatorNumbers;

    public CalculatorNumberGroups(List<CalculatorNumber> calculatorNumbers) {
        this.calculatorNumbers = new ArrayList<>(calculatorNumbers);
    }

    public int addAllCalculatorNumber() {
        return calculatorNumbers.stream()
                .collect(Collectors.summingInt(CalculatorNumber::getNumber));
    }
}
