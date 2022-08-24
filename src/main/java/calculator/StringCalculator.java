package calculator;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\n(.*)");
    private static final String DEFAULT_DELIMITERS = "[,|:]";

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        if (isSingleCharacter(text)) {
            return Integer.parseInt(text);
        }
        return calculate(text);
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private boolean isSingleCharacter(String text) {
        return text.length() == 1;
    }

    private int calculate(final String text) {
        String delimiter = DEFAULT_DELIMITERS;
        String formula = text;

        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            formula = matcher.group(2);
        }

        return sum(toPositiveNumbers(removeZero(formula.split(delimiter))));
    }

    private List<String> removeZero(String[] tokens) {
        return Arrays.stream(tokens)
            .filter(token -> !"0".equals(token))
            .collect(toList());
    }

    private List<PositiveNumber> toPositiveNumbers(List<String> tokens) {
        return tokens.stream()
            .map(PositiveNumber::new)
            .collect(toList());
    }

    private int sum(List<PositiveNumber> positiveNumbers) {
        return positiveNumbers.stream()
            .mapToInt(PositiveNumber::getValue)
            .sum();
    }
}
