package calculator;

import java.util.List;

class StringCalculator {

    StringCalculator() {
    }

    public static int add(final String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        final var numbers = NumberExtractor.extract(input).stream()
            .map(Integer::parseInt)
            .toList();

        validatePositiveNumber(numbers);

        return numbers.stream()
            .reduce(Math::addExact)
            .orElse(0);
    }

    private static void validatePositiveNumber(final List<Integer> numbers) {
        numbers.stream()
            .filter(number -> number < 0)
            .findAny()
            .ifPresent(number -> {
                throw new RuntimeException(
                    String.format("입력에 음수가 포함되어 있습니다. : %d", number)
                );
            });
    }
}
