package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int STRING_TO_SPLIT_INDEX = 2;
    private static final int DEFAULT_OUTPUT = 0;
    private static final Pattern CUSTOM_DELIMITER_PATTREN = Pattern.compile(CUSTOM_DELIMITER_REGEX);


    public int add(final String text) {
        if (isEmpty(text)) {return DEFAULT_OUTPUT;}

        return new TokenSum(findTokens(text)).getSum();
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private String[] findTokens(final String text) {
        Matcher m = CUSTOM_DELIMITER_PATTREN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            return m.group(STRING_TO_SPLIT_INDEX).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }
}


