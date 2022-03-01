package stringcalculator.operator;

import stringcalculator.Number;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static stringcalculator.Number.ZERO;

public class DefaultDelimiterOperator implements Operator {

    private static final Pattern DEFAULT_DELIMITER = Pattern.compile("[,:]");
    private final List<Number> inputNumbers;

    public DefaultDelimiterOperator(String source) {
        this.inputNumbers = toNumbers(source);
    }

    private List<Number> toNumbers(String input) {
        return Arrays.stream(DEFAULT_DELIMITER
                        .split(input))
                .map(Number::new)
                .collect(Collectors.toList());
    }

    @Override
    public int add() {
        return inputNumbers.stream()
                .reduce(ZERO, Number::sum)
                .getValue();
    }
}
