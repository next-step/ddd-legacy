package calculator;

import java.util.Arrays;
import java.util.List;
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

        List<Integer> numbers = extractNumbers(input);;
        validatePositiveNumbers(numbers);

        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private void validatePositiveNumbers(List<Integer> numbers) {
        if (numbers.stream().allMatch(number -> number < 0)) {
            throw new RuntimeException("입력 숫자는 양수여야 합니다.");
        }
    }

    private List<Integer> extractNumbers(String input) {
        Matcher customMatcher = CUSTOM_PATTERN.matcher(input);
        if (customMatcher.find()) {
            String customDelimiter = customMatcher.group(1);
            String[] numbers = customMatcher.group(2).split(customDelimiter);
            return Arrays.stream(numbers)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return Arrays.stream(input.split(DEFAULT_DELIMITER))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
