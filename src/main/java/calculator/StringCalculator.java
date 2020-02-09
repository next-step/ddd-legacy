package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern DELIMETER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String BASIC_DELIMITER = ",|:";

    public int add(String text) {

        if (isValidateString(text)) {
            return 0;
        }

        int sum = 0;
        String delimiter = getDelimiter(text);
        String str = getText(text);
        String[] values = str.split(delimiter);

        Arrays.stream(values).filter(this::isNegativeValue).findAny()
                .orElseThrow(() -> new RuntimeException("음수는 올 수 없습니다."));
        ;

        for (String value : values) {

            sum += Integer.valueOf(value);
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
        if (str == null) {
            return true;
        }
        if (str.isEmpty()) {
            return true;
        }
        return false;

    }
}
