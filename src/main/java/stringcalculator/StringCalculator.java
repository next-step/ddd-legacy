package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            // not a single number? continue
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens).mapToInt(Integer::parseInt).sum();
        }

        return Arrays.stream(text.split("[,:]"))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
