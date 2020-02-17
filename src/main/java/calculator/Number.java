package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Number {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+]?\\d+");
    private int value;

    Number(List<String> strings) {
        this.value = sum(strings);
    }

    private Number(String stringNumber) {
        this.value = parsePositiveInt(stringNumber);
    }

    public int getValue() {
        return this.value;
    }

    private int sum(List<String> strings) {
        for (String string : strings) {
            add(new Number(string));
        }
        return this.value;
    }

    private void add(Number added) {
        this.value += added.getValue();
    }

    private Integer parsePositiveInt(String string) {
        Matcher isNumeric = NUMERIC_PATTERN.matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }
}
