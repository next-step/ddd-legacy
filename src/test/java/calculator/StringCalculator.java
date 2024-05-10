package calculator;

import java.util.List;
import java.util.stream.Stream;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = ",";

    public int add(final String expression) {
        return getNumbers(expression).stream()
            .reduce(Math::addExact)
            .orElse(0);
    }

    private List<Integer> getNumbers(final String expression) {
        return Stream.of(expression.split(DEFAULT_DELIMITER))
            .map(Integer::parseInt)
            .toList();
    }
}
