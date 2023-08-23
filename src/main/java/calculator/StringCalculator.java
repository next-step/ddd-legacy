package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final String DEFAULT_PATTERN = "[,:]";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int ZERO = 0;
    private static final List<Integer> EMPTY_NUMBERS = List.of(ZERO);

    public int add(final String expression) {
        final List<Integer> numbers = extractNumbers(expression);

        return PositiveNumbers.of(numbers)
                .sum()
                .getValue();
    }

    private List<Integer> extractNumbers(final String expression) {
        if (expression == null || expression.isBlank()) {
            return EMPTY_NUMBERS;
        }

        final Matcher matcher = CUSTOM_PATTERN.matcher(expression);

        if (matcher.find()) {
            final String customDelimiter = matcher.group(1);
            final String[] tokens = matcher.group(2).split(customDelimiter);

            return Arrays.stream(tokens)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return Arrays.stream(expression.split(DEFAULT_PATTERN))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
