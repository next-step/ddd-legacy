package calculator;

import java.util.Arrays;

public class StringCalculator {

    public int calculate(SplitStrategy strategy, String input) {
        String[] splitInput = strategy.split(input);

        if (splitInput.length == 1) {
            return Integer.valueOf(splitInput[0]);
        }

        int result = Arrays.stream(splitInput)
                .mapToInt(Integer::valueOf)
                .sum();

        return result;
    }
}