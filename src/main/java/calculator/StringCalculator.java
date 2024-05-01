package calculator;

import java.util.Arrays;
import java.util.List;

public class StringCalculator {

    public int calculate(SplitStrategy strategy, String input) {
        List<Integer> splitInput = strategy.split(input);

        if (splitInput.size() == 1) {
            return splitInput.get(0);
        }

        return splitInput.stream()
                .mapToInt(value -> value)
                .sum();
    }
}