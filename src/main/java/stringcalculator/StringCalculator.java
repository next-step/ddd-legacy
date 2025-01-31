package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {


    public int add(String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        String[] numbers = StringSplit(text);

        return sum(numbers);
    }

    private String[] StringSplit(String text) {

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split("[,:]");
    }

    private int sum (String[] numbers) {
        int result = 0;

        for(String number : numbers) {
            result += Integer.parseInt(number);
        }

        return result;
    }
}
