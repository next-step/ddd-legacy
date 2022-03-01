package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final int EMPTY_NUMBER = 0;
    private static final int SINGLE_INPUT_LENGTH = 1;
    private static final String TOKEN_DELIMITER = ",|:";

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int CUSTOM_DELIMITER_INPUT_INDEX = 2;

    public int add(String text) {
        if (text == null  || text.isEmpty()) {
            return EMPTY_NUMBER;
        }
        if (text.length() == SINGLE_INPUT_LENGTH && isInteger(text)) {
            return Integer.parseInt(text);
        }
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
            String[] tokens = matcher.group(CUSTOM_DELIMITER_INPUT_INDEX).split(customDelimiter);
            return sum(tokens);
        }
        return sum(text.split(TOKEN_DELIMITER));
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int sum(String[] tokens) {
        int sum = EMPTY_NUMBER;
        for (String token : tokens) {
            sum+=Integer.parseInt(token);
        }
        return sum;
    }
}
