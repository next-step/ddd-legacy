package calculator.strategy;

import calculator.Numbers;

import static calculator.Numbers.ZERO_NUMBERS;

public class ZeroNumbersSplitStrategy implements NumbersSplitStrategy {
    @Override
    public Numbers extract(String input) {
        return ZERO_NUMBERS;
    }

    @Override
    public boolean isMatchPattern(String input) {
        return input == null || input.isBlank();
    }
}
