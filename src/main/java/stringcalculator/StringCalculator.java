package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] values = new String[0];
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            values = m.group(2).split(customDelimiter);
        } else {
            values = text.split("[,:]");
        }

        long negativeNumberCnt = Arrays.stream(values)
                .mapToInt(Integer::parseInt)
                .filter(val -> val < 0)
                .count();
        if (negativeNumberCnt > 0) {
            throw new RuntimeException();
        }

        return Arrays.stream(values)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
