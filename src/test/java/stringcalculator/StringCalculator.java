package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        if (text.length() == 1 && Character.isDigit(text.charAt(0))) {
            return Integer.parseInt(text);
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens)
                    .map(this::parseInt)
                    .reduce(0, Integer::sum);
        }

        return Arrays.stream(text.split("[,:]"))
                .map(this::parseInt)
                .reduce(0, Integer::sum);
    }

    private int parseInt(String text) {
        int parsed = Integer.parseInt(text);
        if (parsed >= 0) {
            return parsed;
        }
        throw new RuntimeException();
    }
}
