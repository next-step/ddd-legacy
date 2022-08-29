package calculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public static final int DEFAULT_VALUE = 0;
    public static final String CUSTOM_REGEX = "//(.)\n(.*)";
    public static final String DEFAULT_REGEX = ",|:";

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
        Matcher matcher = Pattern.compile(CUSTOM_REGEX).matcher(input);
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
        int iValue = Integer.parseInt(value);

        if (iValue < 0) {
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
