package calculator.domain;

import java.util.List;

public interface SplitStrategy {
    List<Integer> split(String input);
    List<Number> splitRefactoring(String input);
}
