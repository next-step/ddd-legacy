package calculator.domain;

import java.util.List;
import java.util.stream.Collectors;

public final class StringCalculator extends Calculator implements AddOperation {

    public StringCalculator(StringCalculatorInputValidator stringCalculatorInputValidator, InputParser parser) {
        super(stringCalculatorInputValidator, parser);
    }

    @Override
    public int add(final String input) {
        if (stringCalculatorInputValidator.isEmpty(input)) {
            return 0;
        }
        stringCalculatorInputValidator.assertValidInput(input);

        List<String> parsedNumbers = parser.parse(input);
        List<Integer> numbers = convertToNumbers(parsedNumbers);

        return sum(numbers);
    }

    private List<Integer> convertToNumbers(final List<String> values) {
        List<Integer> numbers = values.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        stringCalculatorInputValidator.assertPositiveNumbers(numbers);
        return numbers;
    }

    private int sum(final List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
