package calculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public StringCalculator() {

    }

    public int add(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        String[] tokens = null;

        if (m.find()) {
            String customDelimiter = m.group(1);
            tokens = m.group(2).split(customDelimiter);
        }

        int sum = 0;
        if (tokens != null) {
            for (String token : tokens) {
                sum = sum + Arrays.stream(token.split("[,:]")).mapToInt(Integer::parseInt).sum();
            }


        } else{
            String[] tokenss = text.split("[,:]");
            for (String token : tokenss) {
                if (Integer.parseInt(token) < 0) {
                    throw new IllegalArgumentException();
                }
            }

            sum = Arrays.stream(tokenss).mapToInt(Integer::parseInt).sum();
        }

        return sum;
    }

}
