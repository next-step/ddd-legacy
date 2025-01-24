package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator implements AddOperation {
    private final Validator validator;
    private final StringCalculatorInputParser parser;

    public StringCalculator(Validator validator, StringCalculatorInputParser parser) {
        this.validator = validator;
        this.parser = parser;
    }

    @Override
    public int add(String input) {
        if (validator.isEmpty(input)) {
            return 0;
        }

        List<String> parsedNumbers = parser.parse(input);
        List<Integer> numbers = convertToNumbers(parsedNumbers);

        return sum(numbers);
    }

    private List<Integer> convertToNumbers(List<String> values) {
        List<Integer> numbers = values.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        validator.assertPositiveNumbers(numbers);
        return numbers;
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
