package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Operands {
    private final List<Operand> operands;

    public Operands(String[] parsedInput) {
        operands = Arrays.stream(parsedInput)
                .map(Operand::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        return operands.stream().map(Operand::getValue)
                .collect(Collectors.toList())
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
