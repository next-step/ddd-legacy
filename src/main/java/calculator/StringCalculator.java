package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] stringNumbers = parseStringToNumbers(text);
        PositiveNumbers positiveNumbers = new PositiveNumbers(stringNumbers);
        return positiveNumbers.sum();
    }

    private String[] parseStringToNumbers(final String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2)
                    .trim()
                    .split(matcher.group(1));
        }

        return text.split(DEFAULT_DELIMITER);
    }
}
