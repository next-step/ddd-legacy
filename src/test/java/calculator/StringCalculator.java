package calculator;

import java.util.List;
import java.util.stream.Stream;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = "(,|:)";

    public int add(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return 0;
        }

        final var numbers = getNumbers(expression);
        numbers.stream()
            .filter(number -> number < 0)
            .findAny()
            .ifPresent(number -> {
                throw new RuntimeException(String.format("음수는 지원하지 않습니다. : %d", number));
            });

        return numbers.stream()
            .reduce(Math::addExact)
            .orElse(0);
    }

    private List<Integer> getNumbers(final String expression) {
        return Stream.of(expression.split(DEFAULT_DELIMITER))
            .map(Integer::parseInt)
            .toList();
    }
}
