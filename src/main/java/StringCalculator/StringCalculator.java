package StringCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String delimiter = "[,:]";
        String numbers = text;

        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (matcher.find()) {
            delimiter = Pattern.quote(matcher.group(1));
            numbers = matcher.group(2);
        }

        String[] tokens = numbers.split(delimiter);
        int sum = 0;
        for (String token : tokens) {
            int number = Integer.parseInt(token);
            if (number < 0) {
                throw new RuntimeException("Negative numbers are not allowed: " + number);
            }
            sum += number;
        }

        return sum;
    }
}
