package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Operand {
    private String operand;
    private List<Integer> numbers;

    public void set(String operand) {
        this.operand = operand;
    }

    public int operation(String operators) {
        int result = 0;

        convert(operators);

        for (int number : numbers) {
            result += number;
        }
        return result;
    }

    private void convert(String operators) {
        numbers = Arrays.stream(operand.split(operators)).map(Integer::parseInt).filter(this::isNegativeNumber).collect(Collectors.toList());
    }

    private boolean isNegativeNumber(Integer number) {
        if (number < 0) {
            throw new RuntimeException("음수");
        }
        return true;
    }

}
