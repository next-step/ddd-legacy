package stringcalculator;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String COMMA_OR_COLON = ",|:";
    private static final int CUSTOM_DELIMITER_NO = 1;
    private static final int CUSTOM_DELIMITER_NUMBERS_NO = 2;
    private static final int MIN_NUMBER_VALUE = 0;


    public int calculate(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_NO);
            String[] numbers = matcher.group(CUSTOM_DELIMITER_NUMBERS_NO).split(customDelimiter);
            return sumStringArray(numbers);
        }

        String[] numbers = input.split(COMMA_OR_COLON);
        return sumStringArray(numbers);
    }

    private int sumStringArray(String[] numbers) {
        if (numbers.length == 1) {
            return convertNumber(numbers[0]);
        }

        return Arrays.stream(numbers)
                .mapToInt(this::convertNumber)
                .sum();
    }

    private int convertNumber(String number) {
        try {
            int result = Integer.parseInt(number);
            validate(result);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    private void validate(int number) {
        if (number < MIN_NUMBER_VALUE) {
            throw new RuntimeException("음수를 입력할 수 없습니다.");
        }
    }
}
