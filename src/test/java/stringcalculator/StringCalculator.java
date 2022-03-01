package stringcalculator;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final Pattern DEFAULT_DELIMITER_PATTERN = Pattern.compile("[,:]");


    public int add(String source) {
        if (isNullOrEmpty(source)) {
            return Number.ZERO.getValue();
        }
        return Arrays.stream(DEFAULT_DELIMITER_PATTERN.split(source))
                .map(Number::new)
                .reduce(Number.ZERO, Number::sum)
                .getValue();
    }

    private boolean isNullOrEmpty(String source) {
        return source == null || source.isEmpty();
    }

    private static class Number {

        private static final Number ZERO = new Number(0);

        private static final int AT_LEAST_POSITIVE_VALUE = 0;
        private static final String MUST_BE_POSITIVE_NUMBER = "value must be positive number";

        private final int value;

        Number(String value) {
            this(Integer.parseInt(value));
        }

        private Number(int value) {
            validateValue(value);
            this.value = value;
        }

        public static Number sum(Number a, Number b) {
            return new Number(Integer.sum(a.value, b.value));
        }

        private void validateValue(int value) {
            if (value < AT_LEAST_POSITIVE_VALUE) {
                throw new RuntimeException(MUST_BE_POSITIVE_NUMBER);
            }
        }

        public int getValue() {
            return value;
        }
    }
}
