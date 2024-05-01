package calculator;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator{


    public static int calculate(String input) {
        if (hasNonNumber(input)) {
            throw new RuntimeException();
        }
        return sum(toInts(split(input)));
    }

    private static boolean hasNonNumber(String input) {
        return input.matches("[a-zA-Z]");
    }


    public static int sum(List<Integer> numbers) {
        return numbers.stream().mapToInt(it -> it).sum();
    }

    private static String[] split(String input) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(input);
        if(!m.find()){
            return input.split("[,:]");
        }

        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }

    private static List<Integer> toInts(String[] values) {
        return Arrays.stream(values)
                .map(Calculator::toInt)
                .toList();
    }

    private static int toInt(String values) {
        int value = Integer.parseInt(values);
        if (value < 0) {
            throw new RuntimeException();
        }
        return value;
    }
}
