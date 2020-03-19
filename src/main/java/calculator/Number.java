package calculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Number {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+]?\\d+");
    private static final Map<Integer, Number> positiveNumbers = new HashMap<>();
    private int value;

    private Number() {
        this.value = 0;
    }

    private Number(int num) {
        this.value = num;
    }

    public static Number sumOf(List<String> strings) {
        Number number = new Number();
        for (String string : strings) {
            number.add(ofPositive(string).value);
        }
        return number;
    }

    private static Number ofPositive(String stringNumber) {
        int num = parsePositiveInt(stringNumber);
        Number number = positiveNumbers.getOrDefault(num, new Number(num));
        positiveNumbers.put(num, number);
        return number;
    }

    private static int parsePositiveInt(String string) {
        Matcher isNumeric = NUMERIC_PATTERN.matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }

    public int getValue() {
        return this.value;
    }

    private void add(int number) {
        this.value += number;
    }
}
