package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final int MIN_VALUE = 0;

    public String[] splitValue(String value) {
        Matcher delimiter = Pattern.compile("//(.)\n(.*)").matcher(value);
        if (delimiter.find()) {
            String customDelimiter = delimiter.group(1);
            String[] values = delimiter.group(2).split(customDelimiter);
            return values;
        }
        return value.split(",|:");
    }

    public void numberCheck(String value) {
        if (Integer.parseInt(value) < MIN_VALUE) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }

    public boolean checkNull(String text) {
        if (text == null || text.equals("")) {
            return true;
        }
        return false;
    }
}
