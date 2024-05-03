package kitchenpos.stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputParser {
    private static final String DEFAULT_DELIMITERS = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int NUMBERS_GROUP = 2;

    private InputParser() {
    }

    public static String[] parse(String input) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(DELIMITER_GROUP);
            String numbers = matcher.group(NUMBERS_GROUP);
            return numbers.split(customDelimiter);
        }

        return input.split(DEFAULT_DELIMITERS);
    }
}
