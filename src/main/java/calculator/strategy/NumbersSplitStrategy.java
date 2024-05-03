package calculator.strategy;

import calculator.Numbers;

public interface NumbersSplitStrategy {
    Numbers extract(String input);

    boolean isMatchPattern(String input);
}
