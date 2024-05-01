package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private String inputString;

    public StringCalculator() {
    }

    public int sum(String inputString) {

        String[] split = inputString.split(",|:");
        return Arrays.stream(split).mapToInt(Integer::parseInt).sum();
    }

    public int getCustomSeperator(String inputString) {
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(inputString);
        int sum = 0;
        if (matcher.find()) {
            String delimiter = matcher.group(1);
            sum = Arrays.stream(matcher.group(2).split(delimiter)).mapToInt(Integer::parseInt).sum();
        }

        return sum;

    }
}
