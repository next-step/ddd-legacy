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
            Number number = convertNumber(numbers[0]);
            return number.getValue();
        }

        return Arrays.stream(numbers)
                .map(this::convertNumber)
                .mapToInt(Number::getValue)
                .sum();
    }

    private Number convertNumber(String number) {
        try {
            return Number.of(Integer.parseInt(number));
        } catch (Exception e) {
            throw e;
        }
    }


}
