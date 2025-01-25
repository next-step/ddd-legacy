package calculator.domain;

import calculator.shared.NumberConvertor;
import calculator.shared.PositiveNumbers;

import java.util.List;

public final class StringCalculator extends Calculator implements AddOperation {

    private final StringCalculatorInputValidator validator;
    private final NumberConvertor numberConvertor;

    public StringCalculator(StringCalculatorInputValidator validator, InputParser parser, NumberConvertor numberConvertor) {
        super(parser);
        this.validator = validator;
        this.numberConvertor = numberConvertor;
    }

    @Override
    public int add(final String input) {
        if (validator.isNullOrBlankInput(input)) {
            return 0;
        }
        validator.assertValidInput(input);

        List<String> parsedNumbers = parser.parse(input);
        PositiveNumbers positiveNumbers = numberConvertor.convertToPositiveNumbers(parsedNumbers);

        return positiveNumbers.sum();
    }

    private int sum(final List<Integer> numbers) {
        return numbers.stream().mapToInt(Integer::intValue).sum();
    }
}
