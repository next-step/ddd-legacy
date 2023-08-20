package calculator.domain;

import calculator.util.Convert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberParser {

    private final Separate separate = new Separate();

    public PositiveNumbers parser(final String input) {
        List<PositiveNumber> operands = Arrays.stream(separate.parser(input))
                .map(Convert::positiveNumber)
                .map(PositiveNumber::new)
                .collect(Collectors.toList());

        return new PositiveNumbers(operands);
    }

}
