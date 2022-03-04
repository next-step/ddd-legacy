package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.Positive.*;

public class StringCalculator implements Calculator {
    private static final String DELIMITERS = "[,:]";
    private static final String CUSTOM_DELIMITERS_PATTERN = "//(.)\n(.*)";

    @Override
    public int add(String text) {
        if (isValidText(text)) {
            return 0;
        }

        int[] operands = parseInt(split(text));
        if (doesHaveNegative(operands)) {
            throw new RuntimeException();
        }

        return sum(operands);
    }

    private boolean isValidText(final String text) {
        return isEmptyOrNull(text);
    }

    private boolean isEmptyOrNull(final String text) {
        return text == null ||
                text.isEmpty();
    }

    private String[] split(String text) {
        String delimiter = DELIMITERS;

        Matcher matcher = Pattern.compile(CUSTOM_DELIMITERS_PATTERN).matcher(text);
        if (matcher.find()) {
            delimiter = matcher.group(1);
            text = matcher.group(2);
        }
        return text.split(delimiter);
    }
}
