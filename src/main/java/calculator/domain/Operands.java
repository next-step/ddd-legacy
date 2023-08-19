package calculator.domain;

import java.util.Collections;
import java.util.List;

public class Operands {

    private final List<Operand> operands;

    public Operands(final List<Operand> operands) {
        this.operands = Collections.unmodifiableList(operands);
    }

    public int sum() {
        return operands.stream()
                .mapToInt(Operand::getNumber)
                .sum();
    }
}
