package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens).mapToInt(Integer::parseInt).sum();
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            String[] values = text.split("[,:]");
            return Arrays.stream(values).mapToInt(Integer::parseInt).sum();
        }
    }
}
