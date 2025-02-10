package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return sum(parse(text));
    }

    private String[] parse(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split("[,:]");
    }

    private int sum(String[] numbers) {
        int total = 0;
        for (String number : numbers) {
            total += parseInt(number);
        }
        return total;
    }

    private int parseInt(String number) {
        int result = Integer.parseInt(number);
        if (result < 0) {
            throw new RuntimeException();
        }
        return result;
    }
}
