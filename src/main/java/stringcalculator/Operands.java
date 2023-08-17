package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Operands {
    private List<Operand> operands;

    public Operands(String[] parsedInput) {
        operands = Arrays.stream(parsedInput)
                .map(Operand::new)
                .collect(Collectors.toList());
    }

    public int sum() {
        return -1;
    }
}
