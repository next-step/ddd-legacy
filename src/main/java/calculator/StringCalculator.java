package calculator;

import java.util.List;

public class StringCalculator {

    public int calculate(SplitStrategy strategy, String stringNumbers) {
        List<Integer> splitNumbers = strategy.split(stringNumbers);
        int result = 0;

        for (Integer number : splitNumbers) {
            handleNegative(number);
            result = result + number;
        }

        return result;
    }

    private void handleNegative(Integer number) {
        if (number < 0) {
            throw new RuntimeException("전달된 수에 음수가 존재합니다.");
        }
    }
}