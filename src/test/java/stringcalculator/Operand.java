package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Operand {
    private String operand;
    private Numbers numbers = new Numbers();

    public void set(String operand) {
        this.operand = operand;
    }

    public int operation(String operators) {
        convert(operators);

        return this.numbers.sum();
    }

    private void convert(String operators) {
        String[] operands = operand.split(operators);
        for (String operand: operands) {
            this.numbers.addNumber(operand);
        }
    }

}
