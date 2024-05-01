package calculator;


import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator{

    private int[] numbers;

    public Calculator(String input) {
        if (hasNonNumber(input)) {
            throw new RuntimeException();
        }
        toInts(split(input));
    }

    private boolean hasNonNumber(String input) {
        return input.matches("[a-zA-Z]");
    }


    public int sum() {
        return Arrays.stream(numbers).sum();
    }

    private static String[] split(String input) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(input);
        if(!m.find()){
            return input.split("[,:]");
        }

        String customDelimiter = m.group(1);
        return m.group(2).split(customDelimiter);
    }

    private void toInts(String[] values) {
        numbers = initArray(values);
        for (int i = 0; i < values.length; i++) {
            numbers[i] = toInt(values[i]);
        }
    }

    private static int toInt(String values) {
        int value = Integer.parseInt(values);
        if (value < 0) {
            throw new RuntimeException();
        }
        return value;
    }


    private static int [] initArray(String[] values) {
        return new int[values.length];
    }
}
