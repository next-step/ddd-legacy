package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringCalculator {

    public int add(final String expression) {
        if (isBlank(expression)) {
            return 0;
        }
        return Arrays.stream(split(expression))
                .map(PositiveInteger::new)
                .reduce(new PositiveInteger(0), PositiveInteger::add)
                .intValue();
    }

    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_IDX = 1;
    private static final int NUMBERS_IDX = 2;

    private static class PositiveInteger {

        public static final int ZERO_VALUE = 0;
        private final int number;

        PositiveInteger(int number) {
            if (number < 0) {
                throw new IllegalArgumentException();
            }
            this.number = number;
        }

        PositiveInteger(String number) {
            this(Integer.parseInt(number));
        }

        PositiveInteger add(PositiveInteger operand) {
            if (operand.number == ZERO_VALUE) {
                return this;
            }
            return new PositiveInteger(this.number + operand.number);
        }

        int intValue() {
            return number;
        }

    }

    private boolean isBlank(String expression) {
        return expression == null || expression.trim().length() == 0;
    }

    private String[] split(String expression) {
        Matcher m = DEFAULT_PATTERN.matcher(expression);
        if (m.find()) {
            String delimiter = m.group(DELIMITER_IDX);
            return m.group(NUMBERS_IDX).split(delimiter);
        }
        return expression.split(DEFAULT_DELIMITER);
    }
}
