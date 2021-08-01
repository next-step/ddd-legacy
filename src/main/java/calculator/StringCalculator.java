package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        return this.calculate(this.parsingDelimiter(text));
    }

    private String[] parsingDelimiter(final String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split("[,:]");
    }

    private int calculate(final String[] numbers) {
        if (numbers.length == 1) {
            return this.stringToInt(numbers[0]);
        }
        int sum = 0;
        for (String number : numbers) {
            sum += stringToInt(number);
        }
        return sum;
    }

    private int stringToInt(final String text) {
        int number = Integer.parseInt(text);

        if (number < 0) {
            throw new RuntimeException();
        }
        return number;
    }
}
