package calculator.strategy;

import calculator.Numbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitStrategy implements NumbersSplitStrategy {

    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int TARGET_TEXT_INDEX = 2;
    private static final String EMPTY_STRING = "";
    private static final String DELIMITER_REGEX = "//(.)\n(.*)";
    private static final Pattern MATCH_PATTERN = Pattern.compile(DELIMITER_REGEX);

    @Override
    public Numbers extract(String input) {
        Matcher matcher = MATCH_PATTERN.matcher(input);
        String customDelimiter = EMPTY_STRING;
        if (matcher.find()) {
            customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
        }
        String targetNumber = matcher.group(TARGET_TEXT_INDEX);
        return new Numbers(targetNumber.split(customDelimiter));
    }

    @Override
    public boolean isMatchPattern(String input) {
        return MATCH_PATTERN.matcher(input).matches();
    }
}
