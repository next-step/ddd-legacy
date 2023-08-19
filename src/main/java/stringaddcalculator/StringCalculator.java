package stringaddcalculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return 0;
        }

        if (text.length() == 1) {
            return Integer.parseInt(text);
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String [] tokens = m.group(2).split(customDelimiter);

            int result = 0;

            for (String value : tokens) {
                result = result + Integer.parseInt(value);
            }
            return result;
        }

        String[] tokens = text.split(",|:");

        int result = 0;

        for (String value : tokens) {
            int num = Integer.parseInt(value);
            if (num < 0) {
                throw new RuntimeException("음수 입력 안 됨");
            }
            result = result + num;
        }
        return result;
    }
}
