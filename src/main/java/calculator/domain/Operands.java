package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Operands {

    private final List<Operand> values;

    public Operands(List<Operand> values) {
        this.values = values;
    }

    public static Operands from(List<String> splitText) {
        return new Operands(
            splitText.stream()
                .map(Operand::from)
                .collect(Collectors.toList())
        );
    }

    public Operand addAll() {
        int sum = values.stream()
            .mapToInt(Operand::getValue)
            .sum();

        return new Operand(sum);
    }
}
