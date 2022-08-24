package stringcalculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {

    private static final String SEPARATOR = ",|:";
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int NUMBER_VALUE_INDEX = 2;

    public StringCalculator() {
    }

    public int sum(final String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new RuntimeException("Null 이거나 공란일 수 없습니다.");
        }

        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(value);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            String[] split = m.group(NUMBER_VALUE_INDEX).split(customDelimiter);
            return sum(split);
        }
        return sum(value.split(SEPARATOR));
    }

    private int sum(final String[] values) {
        return Stream.of(values)
                .parallel()
                .map(PositiveNumber::new)
                .mapToInt(PositiveNumber::toInt)
                .sum();
    }
}
