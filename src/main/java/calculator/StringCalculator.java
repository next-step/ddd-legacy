package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) throws RuntimeException {

        if (text == null || text.isEmpty()) return 0;

        String[] tokens = getNumbers(text);

        int sum = 0;
        for (String i : tokens) {
            if (Integer.parseInt(i) < 0) {
                throw new RuntimeException();
            }
            sum += Integer.parseInt(i);
        }
        return sum;
    }

    private String[] getNumbers(String text) {
        String delimiter = ",|:";;
        String numberString = text ;

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            delimiter = m.group(1);
            numberString = m.group(2);
        }
        return numberString.split(delimiter);
    }

}
