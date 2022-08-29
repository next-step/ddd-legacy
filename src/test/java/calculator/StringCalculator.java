package calculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int DEFAULT_VALUE = 0;
    private static final String CUSTOM_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_REGEX = ",|:";
    private static final Pattern pattern = Pattern.compile(CUSTOM_REGEX);

    public int sum(final String input) {
        if (Objects.isNull(input) || input.isBlank()) {
            return DEFAULT_VALUE;
        }

        String[] numbers = parseInput(input);

        return Arrays.stream(numbers)
                .mapToInt(this::parseInt)
                .sum();
    }

    private String[] parseInput(final String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String customInput = matcher.group(2);
            return customInput.split(matcher.group(1));
        }
        return input.split(DEFAULT_REGEX);
    }

    private int parseInt(final String value) {
        checkNumber(value);
        checkNegative(value);
        return Integer.parseInt(value);
    }

    private void checkNegative(final String value) {
        int parseIntValue = Integer.parseInt(value);

        if (parseIntValue < 0) {
            throw new RuntimeException("문자열 계산기에서 음수값은 허용되지 않습니다.");
        }
    }

    private void checkNumber(final String value) {
        boolean isNumber = value.matches("\\d");

        if (!isNumber) {
            throw new RuntimeException("숫자 이외의 값은 허용되지 않습니다.");
        }
    }
}
