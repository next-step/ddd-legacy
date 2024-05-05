package stringcalculator;


import java.util.ArrayList;
import java.util.List;

public class NumericString {

    public static final int ZERO = 0;
    private static final String SINGLE_INTEGER_REGEX = "^-?\\d+$";

    private final String numeric;

    public NumericString(final String numeric) {
        this.numeric = numeric;
    }

    public int sum() {
        return isSingleNumber() ? parseSingleNumber().sum() : parseMultipleNumbers().sum();
    }

    private PositiveNumbers parseSingleNumber() {
        return PositiveNumbers.of(this.numeric);
    }

    private PositiveNumbers parseMultipleNumbers() {
        StringSplitOption splitOption = StringSplitOption.find(this.numeric);
        List<PositiveNumber> numbers = new ArrayList<>();
        for (String number : splitOption.split(this.numeric)) {
            numbers.add(PositiveNumber.byString(number));
        }
        return new PositiveNumbers(numbers);
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
