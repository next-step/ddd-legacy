package stringcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toList;

public class Operands {
    private List<Operand> operands = new ArrayList<>();

    public Operands(List<Operand> operands) {
        this.operands = operands;
    }

    public static Operands of(List<String> operands) {
        return new Operands(operands.stream()
                .map(Operand::new)
                .collect(toList()));
    }

    public int calculate(BinaryOperator<Integer> operator) {
        Operand result = new Operand();
        for (Operand operand : operands) {
            result = result.operate(operand, operator);
        }
        return result.getValue();
    }
}
