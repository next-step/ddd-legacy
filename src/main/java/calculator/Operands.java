package calculator;

import com.sun.istack.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Operands {
    private final List<Integer> operandList = new ArrayList<>();

    public Operands(@NotNull String[] operands) {
        for (String operand : operands) {
            int number = parseInt(operand);
            checkNegative(number);
            operandList.add(number);
        }
    }

    private int parseInt(String number) throws RuntimeException {
        int resultNumber;
        try {
            resultNumber = Integer.parseInt(number);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("operand is not number format");
        }
        return resultNumber;
    }

    private void checkNegative(int number) throws RuntimeException {
        if(number < 0) {
            throw new RuntimeException();
        }
    }

    public int sum() {
        int result = 0;
        for (int number : operandList) {
            result += number;
        }
        return result;
    }
}
