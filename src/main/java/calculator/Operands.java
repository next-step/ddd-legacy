package calculator;

import java.util.List;

public class Operands {
    private List<Operand> numbers;

    public Operands() {}

    public Operands(List<Operand> values) {
        this.numbers = values;
    }

    public int addAll() {
        return this.numbers.stream()
                .reduce(Operand::add)
                .orElse(Operand.valueOf(0))
                .intValue();
    }
}
