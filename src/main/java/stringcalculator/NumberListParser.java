package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberListParser {
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String COMMA_OR_COLON = ",|:";
    private static final int CUSTOM_DELIMITER_NO = 1;
    private static final int CUSTOM_DELIMITER_NUMBERS_NO = 2;

    public static NumberList parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_NO);
            String[] numberStringArray = matcher.group(CUSTOM_DELIMITER_NUMBERS_NO).split(customDelimiter);
            return NumberList.of(numberStringArray);
        }
        return NumberList.of(input.split(COMMA_OR_COLON));
    }
}
