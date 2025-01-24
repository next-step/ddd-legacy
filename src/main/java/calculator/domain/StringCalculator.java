package calculator.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {
    private final Validator validator;
    private final InputParser parser;

    public StringCalculator(Validator validator, InputParser parser) {
        this.validator = validator;
        this.parser = parser;
    }

    public int add(String input) {
        if (validator.isEmpty(input)) {
            return 0;
        }

        String[] values = parser.parse(input);
        List<Integer> numbers = convertToNumbers(values);

        return sum(numbers);
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
