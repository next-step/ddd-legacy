package calculator;

import java.util.Arrays;

class StringCalculator {

    StringCalculator() {
    }

    public int add(final String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        final var numbers = StringExpressionTokenizer.tokenize(input);

        return numbers.stream()
            .mapToInt(Integer::parseInt)
            .reduce(Math::addExact)
            .orElse(0);
    }
}
