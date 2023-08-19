package stringcalculator;

import io.micrometer.core.instrument.util.StringUtils;

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
            String[] numberStringArray = matcher.group(CUSTOM_DELIMITER_NUMBERS_NO).split(customDelimiter);
            NumberList numberList = NumberList.of(numberStringArray);
            return numberList.sum();
        }
        NumberList numberList = NumberList.of(input.split(COMMA_OR_COLON));
        return numberList.sum();
    }

}
