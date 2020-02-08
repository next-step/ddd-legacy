package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    static final int ZERO = 0;

    int add(final String text) {
        if (text == null || text.isEmpty()) {
            return ZERO;
        }

        String[] stringNumbers = text.split("[,:]");

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            stringNumbers = m.group(2).split(customDelimiter);
        }

        return Arrays.stream(stringNumbers).map(Integer::parseInt).filter(a -> {
            if (a < 0) {
                throw new RuntimeException();
            }
            return true;
        }).reduce(0, Integer::sum);
    }

}
