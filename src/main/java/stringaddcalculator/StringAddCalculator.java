package stringaddcalculator;

import java.util.Arrays;

public class StringAddCalculator {
    private final Separator separator = new Separator();

    public int add(String expression) {
        if (expression == null || expression.isBlank()) {
            return 0;
        }

        return Arrays.stream(separator.separate(expression))
                .sum();
    }
}
