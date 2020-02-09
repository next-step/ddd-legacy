package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern DELIMETER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String BASIC_DELIMITER = ",|:";

    public int calculate(String text) {

        if (!isValidateString(text)) {
            return 0;
        }

        int sum = 0;
        String delimiter = getDelimiter(text);
        String str = getText(text);
        String[] values = str.split(delimiter);
        PositiveNumbers positiveNumbers = new PositiveNumbers(values);

        for (Integer value : positiveNumbers.getNumbers()) {

            sum += value;
        }

        return sum;

    }

    private boolean isNegativeValue(String value) {
        return Integer.parseInt(value) < 0;
    }

    private String getText(String str) {
        Matcher m = DELIMETER_PATTERN.matcher(str);
        if (m.find()) {
            return m.group(2);
        }
        return str;
    }

    private String getDelimiter(String str) {

        Matcher m = DELIMETER_PATTERN.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return BASIC_DELIMITER;
    }

    private boolean isValidateString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return true;

    }
}
