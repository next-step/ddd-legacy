package calculator;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {

    private static final String ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN = "[^0-9\\s]";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(
        String.format("^//%s\n(.*)", ANY_WORD_WITHOUT_NUMBER_AND_SPACE_PATTERN)
    );

    private static final String NUMBER_PATTERN = "[1-9][d]*";
    private static final String DEFAULT_DELIMITER = ",|:";

    StringCalculator() {
    }

    public int add(final String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        final var matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);

        String delimiter;
        String expression;

        if (matcher.matches()) {
            delimiter = matcher.group(0)
                .substring(2, 3);
            expression = matcher.group(1);
        } else {
            delimiter = DEFAULT_DELIMITER;
            expression = input;
        }

        final var expressionPattern = String.format(
            "^%s[(%s)%s]*$",
            NUMBER_PATTERN,
            delimiter,
            NUMBER_PATTERN
        );

        if (!expression.matches(expressionPattern)) {
            throw new IllegalArgumentException("입력을 해석할 수 없습니다.");
        }

        final var numbers = Stream.of(expression.split(delimiter))
            .map(Integer::parseInt)
            .toList();

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
}
