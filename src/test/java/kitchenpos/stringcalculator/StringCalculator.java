package kitchenpos.stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (customDelimiterMatcher.find()) {
            String customDelimiter = customDelimiterMatcher.group(DELIMITER_GROUP);
            String numbers = customDelimiterMatcher.group(NUMBERS_GROUP);
            return sumNumbers(splitNumbers(numbers, customDelimiter));
        }

        return sumNumbers(splitNumbers(input, DEFAULT_DELIMITERS));
    }

    /**
     * 입력된 문자열을 주어진 구분자 패턴을 사용해 분리한다.
     * <p>
     * 구분자를 기준으로 문자열을 여러 숫자 문자열로 나누고, 이를 배열로 반환한다.
     */
    private String[] splitNumbers(String input, String delimiterPattern) {
        return input.split(delimiterPattern);
    }

    /**
     * 분리된 숫자 문자열을 합산해 최종 결과를 반환한다.
     * <p>
     * 음수거나 숫자가 아닌 문자가 포함되어 있다면 IllegalArgumentException을 던진다.
     */
    private int sumNumbers(String[] numberStrings) {
        return Arrays.stream(numberStrings)
                     .mapToInt(Integer::parseInt)
                     .peek(this::validateNegativeNumber)
                     .sum();
    }

    private void validateNegativeNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수는 포함될 수 없습니다: " + number);
        }
    }
}
