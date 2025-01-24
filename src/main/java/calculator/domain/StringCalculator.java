package calculator.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class StringCalculator {
    private final Validator validator;

    public StringCalculator(Validator validator) {
        this.validator = validator;
    }

    public int add(String input) {
        if (validator.isEmpty(input)) {
            return 0;
        }

        String[] values = parseInput(input);
        List<Integer> numbers = convertToNumbers(values);

        return sum(numbers);
    }

    private String[] parseInput(String text) {
        if (text.startsWith("//")) {
            String[] tokens = text.split("\n", 2);
            String customDelimiter = tokens[0].substring(2);
            return tokens[1].split(customDelimiter);
        }

        return text.split(",|:");
    }

    private List<Integer> convertToNumbers(String[] values) {
        List<Integer> numbers = Arrays.stream(values)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        validator.assertPositiveNumbers(numbers);
        return numbers;
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}