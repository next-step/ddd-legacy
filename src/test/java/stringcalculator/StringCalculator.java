package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class StringCalculator {
    public int add(String rawStr) {
        if (rawStr == null || rawStr.isEmpty()) {
            return 0;
        }

        Pattern pattern = Pattern.compile("//(.*)\\n(.*)");
        Matcher matcher = pattern.matcher(rawStr);
        String target = rawStr;
        List<String> splitters = new ArrayList<>(List.of(",", ":"));
        if(matcher.matches()) {
            String splitter = matcher.group(1);
            target = matcher.group(2);
            splitters.add(splitter);
        }

        String temp = String.join("", splitters);
        int sum = Arrays.stream(target.split("[" + temp + "]")).mapToInt(Integer::parseInt).peek(num -> {
            if (num < 0) {
                throw new RuntimeException();
            }
        }).sum();

        return sum;
    }
}
