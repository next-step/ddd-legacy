package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {

        if (isValidateString(text)) {
            return 0;
        }

        int sum = 0;
        String delimiter = getDelimiter(text);
        String str = getText(text);
        String[] values = str.split(delimiter);

        for (String value : values) {

            sum += Integer.valueOf(value);
        }

        return sum;

    }

    private String getText(String str) {
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(2);
        }
        return str;
    }

    private String getDelimiter(String str) {

        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return ",|:";
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
