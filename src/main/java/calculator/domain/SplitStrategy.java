package calculator.domain;

import java.util.List;

public interface SplitStrategy {
    List<Number> split(String input);
}
