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
            int parsedIntegerFromText = Integer.parseInt(text);
            if (parsedIntegerFromText < 0) {
                throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            // not a single number? continue
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            for (String t : tokens) {
                if (Integer.parseInt(t) < 0) {
                    throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
                }
            }
            return Arrays.stream(tokens).mapToInt(Integer::parseInt).sum();
        }

        for (String t : text.split("[,:]")) {
            if (Integer.parseInt(t) < 0) {
                throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
            }
        }
        return Arrays.stream(text.split("[,:]"))
            .mapToInt(Integer::parseInt)
            .sum();
    }
}
