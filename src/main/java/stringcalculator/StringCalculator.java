package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        String[] inputValues = splitAndValidate(text);

        return sum(inputValues);
    }

    private static String[] splitAndValidate(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        String[] values;
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            values = m.group(2).split(customDelimiter);
        } else {
            values = text.split("[,:]");
        }
        return values;
    }

    private static int sum(String[] values) {
        return Arrays.stream(values)
                .mapToInt(val -> Number.of(val).number())
                .sum();
    }
}
