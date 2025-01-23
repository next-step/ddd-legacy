package calculator;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.micrometer.common.util.StringUtils.isBlank;

public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        String[] numbers = parseInput(input);
        validateNoNegativeNumbers(numbers);
        return calculateSum(numbers);
    }

    private String[] parseInput(String input) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.matches()) {
            String delimiter = matcher.group(1);
            return matcher.group(2).split(delimiter);
        }
        return input.split(DEFAULT_DELIMITERS);
    }

    private void validateNoNegativeNumbers(String[] numbers) {
        List<Integer> negativeNumbers = Arrays.stream(numbers)
                .map(Integer::parseInt)
                .filter(num -> num < 0)
                .toList();

        if (!negativeNumbers.isEmpty()) {
            throw new IllegalArgumentException("Negative numbers not allowed: " + negativeNumbers);
        }
    }

    private int calculateSum(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
