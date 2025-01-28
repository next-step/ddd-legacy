package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        int result = 0;

        String[] numberString = splitNumbers(text);
        result = Arrays.stream(numberString).mapToInt(Integer::parseInt).sum();

        return result;
    }

    public String[] splitNumbers(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        String delimiters = ",|:";

        if (m.find()) {
            String customDelimiter = m.group(1);
            delimiters = delimiters + "|" + customDelimiter;
            text = m.group(2);
        }

        return text.split(delimiters);
    }
}
