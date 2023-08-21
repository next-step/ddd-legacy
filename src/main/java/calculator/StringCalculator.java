package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile(CUSTOM_DELIMITER);
    private static final String DEFAULT_DELIMITER = "[,:]";

    public int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        PositiveNumbers positiveNumbers = extractNumbers(input);

        return positiveNumbers.addTotalPositiveNumbers()
                .getNumber();
    }

    private PositiveNumbers extractNumbers(String input) {
        Matcher customMatcher = CUSTOM_PATTERN.matcher(input);
        if (customMatcher.find()) {
            String customDelimiter = customMatcher.group(1);
            String[] numbers = customMatcher.group(2).split(customDelimiter);
            return new PositiveNumbers(Arrays.stream(numbers)
                    .map(Integer::parseInt)
                    .map(PositiveNumber::new)
                    .collect(Collectors.toList()));
        }
        return new PositiveNumbers(Arrays.stream(input.split(DEFAULT_DELIMITER))
                .map(Integer::parseInt)
                .map(PositiveNumber::new)
                .collect(Collectors.toList()));
    }
}
