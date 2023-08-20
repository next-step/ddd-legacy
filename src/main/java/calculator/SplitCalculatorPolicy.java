package calculator;

import java.util.Arrays;

public class SplitCalculatorPolicy implements CalculatorPolicy {

    private static final String SEPARATOR = ",|:";

    @Override
    public boolean isSupport(String text) {
        return text.contains(",") || text.contains(":");
    }

    @Override
    public int calculate(String text) {
        return Arrays.stream(text.split(SEPARATOR))
                .mapToInt(this::toPositive)
                .sum();
    }
}
