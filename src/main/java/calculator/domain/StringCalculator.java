package calculator.domain;

import java.util.List;

public final class StringCalculator extends Calculator implements AddOperation {

    private final StringCalculatorInputValidator validator;

    public StringCalculator(StringCalculatorInputValidator validator, InputParser parser) {
        super(parser);
        this.validator = validator;
    }

    @Override
    public int add(final String input) {
        if (validator.isNullOrBlankInput(input)) {
            return 0;
        }
        validator.assertValidInput(input);

        List<String> parsedNumbers = parser.parse(input);
        List<Integer> numbers = convertToNumbers(parsedNumbers);

        return sum(numbers);
    }

    private List<Integer> convertToNumbers(final List<String> values) {
        List<Integer> numbers = values.stream()
                .map(Integer::parseInt)
                .toList();

        validator.assertPositiveNumbers(numbers);
        return numbers;
    }

    private int sum(final List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
