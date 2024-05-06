package stringcalculator;


import java.util.Arrays;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class NumericString {
    
    private static final String SINGLE_INTEGER_REGEX = "^-?\\d+$";

    private final String numeric;

    public NumericString(final String numeric) {
        this.numeric = numeric;
    }

    public PositiveNumber sum() {
        return isSingleNumber() ? parseSingleNumber().sum() : parseMultipleNumbers().sum();
    }

    private PositiveNumbers parseSingleNumber() {
        return PositiveNumbers.of(this.numeric);
    }

    private PositiveNumbers parseMultipleNumbers() {
        StringSplitOption splitOption = StringSplitOption.find(this.numeric);
        return Arrays.stream(splitOption.split(this.numeric))
                .map(PositiveNumber::byString)
                .collect(collectingAndThen(toList(), PositiveNumbers::new));
    }

    public boolean isEmpty() {
        return numeric == null || numeric.isEmpty();
    }

    private boolean isSingleNumber() {
        return this.numeric.matches(SINGLE_INTEGER_REGEX);
    }

    public String value() {
        return numeric;
    }
}
