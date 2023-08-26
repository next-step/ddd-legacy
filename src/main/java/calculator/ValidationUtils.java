package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final int MIN_VALUE = 0;

    public static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    public static final String DEFAULT_REGEX = ",|:";
    public static final int DELIMITER_GROUP = 1;
    public static final int VALUE_GROUP = 2;

    public static String[] splitValue(String value) {
        Matcher delimiter = CUSTOM_DELIMITER_PATTERN.matcher(value);
        if (delimiter.find()) {
            String customDelimiter = delimiter.group(DELIMITER_GROUP);
            String[] values = delimiter.group(VALUE_GROUP).split(customDelimiter);
            return values;
        }
        return value.split(DEFAULT_REGEX);
    }

    public static void numberCheck(String value) {
        if (Integer.parseInt(value) < MIN_VALUE) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }

    public static boolean checkNull(String text) {
        if (text == null || text.equals("")) {
            return true;
        }
        return false;
    }
}
