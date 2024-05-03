package calculator;

import java.util.regex.Pattern;

public class Number {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

    private final int number;

    public Number(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("not allowed negative number");
        }
        this.number = number;
    }

    public static Number of(String number) {
        if (isNotNumber(number)) {
            throw new IllegalArgumentException("invalid number string: " + number);
        }
        return new Number(Integer.parseInt(number));
    }

    public int value() {
        return number;
    }

    private static boolean isNotNumber(String number) {
        return !NUMBER_PATTERN.matcher(number).find();
    }
}
