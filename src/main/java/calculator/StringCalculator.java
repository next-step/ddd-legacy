package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {

        final int ZERO = 0;
        int result = 0;

        if (text == null || text.isEmpty()) {
            return ZERO;
        }

        if(isNumber(text)){
            result = Integer.parseInt(text);
        }

        if (text.contains(",") | text.contains(":")) {
            String[] split = text.split(",|:");
            result= Arrays.stream(split).mapToInt(Integer::parseInt).sum();
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if(m.find()){
            String customerDelimiter = m.group(1);
            String[] split = m.group(2).split(customerDelimiter);
            result =  Arrays.stream(split).mapToInt(Integer::parseInt).sum();
        }

        return result;
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

}
