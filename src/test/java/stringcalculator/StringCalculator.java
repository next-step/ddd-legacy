package stringcalculator;

import java.util.Objects;

public class StringCalculator {

    private static final int EMPTY_VALUE = 0;

    public StringCalculator() {
    }

    public int sum(final String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            return EMPTY_VALUE;
        }

        return new Splitter().split(value)
                .stream()
                .parallel()
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::toInt)
                .sum();
    }
}
