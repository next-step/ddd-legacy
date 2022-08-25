package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = "[,:]";

    public int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return calculate(matcher.group(2), customDelimiter);
        }
        return calculate(input, DEFAULT_DELIMITER);
    }

    private int calculate(String input, String delimiter) {
        String[] tokens = input.split(delimiter);
        StringNumber sum = new StringNumber();

        for (String number : tokens) {
            sum = sum.add(new StringNumber(number));
        }
        return sum.getNumber();
    }
}
