package calculator;

import java.util.regex.Pattern;

public class Number {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

    private final int number;

    public Number(String number) {
        if (isNotNumber(number)) {
            throw new IllegalArgumentException("invalid number string: " + number);
        }
        this.number = toInt(number);
        if (isNegative()) {
            throw new IllegalArgumentException("not allow negative number: " + number);
        }
    }

    public int value() {
        return number;
    }

    private boolean isNotNumber(String number) {
        return !NUMBER_PATTERN.matcher(number).find();
    }

    private int toInt(String values) {
        return Integer.parseInt(values);
    }

    private boolean isNegative() {
        return number < 0;
    }
}
