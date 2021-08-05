package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class Numbers {
    private List<Integer> numbers;

    public Numbers() {
        this.numbers = new ArrayList<>();
    }

    public void addNumber(String operand) {
        int number = Integer.parseInt(operand);
        isNegativeNumber(number);
        this.numbers.add(number);
    }

    private void isNegativeNumber(Integer number) {
        if (number < 0) {
            throw new RuntimeException("음수");
        }
    }

    public int sum() {
        int result = 0;
        for (int number: this.numbers) {
            result += sum();
        }
        return result;
    }
}
