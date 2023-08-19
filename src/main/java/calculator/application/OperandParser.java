package calculator.application;

import calculator.domain.Operand;
import calculator.domain.Operands;
import calculator.util.Convert;
import calculator.util.Separate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class OperandParser {

    public Operands parser(final String input) {
        List<Operand> operands = Arrays.stream(Separate.parser(input))
                .map(Convert::positiveNumber)
                .map(Operand::new)
                .collect(Collectors.toList());

        return new Operands(operands);
    }

}
