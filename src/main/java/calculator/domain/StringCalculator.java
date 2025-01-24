package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public final class StringCalculator extends Calculator implements AddOperation {

    public StringCalculator(Validator validator, StringCalculatorInputParser parser) {
        super(validator, parser);
    }

    @Override
    public int add(final String input) {
        if (validator.isEmpty(input)) {
            return 0;
        }

        List<String> parsedNumbers = parser.parse(input);
        List<Integer> numbers = convertToNumbers(parsedNumbers);

        return sum(numbers);
    }

    private List<Integer> convertToNumbers(final List<String> values) {
        List<Integer> numbers = values.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        validator.assertPositiveNumbers(numbers);
        return numbers;
    }

    private int sum(final List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
