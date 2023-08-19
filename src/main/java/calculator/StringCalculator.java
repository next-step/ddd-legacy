package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = ",|:";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int STRING_TO_SPLIT_INDEX = 2;
    private static final int DEFAULT_OUTPUT = 0;

    private final Pattern customDelemiterPattern;

    public StringCalculator() {
        customDelemiterPattern = Pattern.compile(CUSTOM_DELIMITER_REGEX);
    }

    public int add(final String text) {
        if (isEmpty(text)) {return DEFAULT_OUTPUT;}

        String[] tokens = findTokens(text);
        int sum = 0;

        for (String token : tokens) {
            int num = Integer.parseInt(token);
            validate(num);
            sum += num;
        }

        return sum;
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private void validate(final int num) {
        if (num < 0) {
            throw new RuntimeException();
        }
    }

    private String[] findTokens(final String text) {
        Matcher m = customDelemiterPattern.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
            return m.group(STRING_TO_SPLIT_INDEX).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }
}


