package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PositiveNumber {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+]?\\d+");
    private int value;

    PositiveNumber(List<String> strings) {
        this.value = sum(strings);
    }

    private PositiveNumber(String stringNumber) {
        this.value = parsePositiveInt(stringNumber);
    }

    public int getValue() {
        return this.value;
    }

    private int sum(List<String> strings) {
        for (String string : strings) {
            add(new PositiveNumber(string));
        }
        return this.value;
    }

    private void add(PositiveNumber added) {
        this.value += added.value;
    }

    private Integer parsePositiveInt(String string) {
        Matcher isNumeric = NUMERIC_PATTERN.matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }
}
