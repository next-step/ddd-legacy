package calculator;

public class Number {
    private static final String NUMBER_REGEX = "^[0-9]+$";
    private int value;

    public Number(String text) {
        validate(text);
        value = Integer.parseInt(text);
    }

    private void validate(String number) {
        if (!number.matches(NUMBER_REGEX) || Integer.parseInt(number) < 0) {
            throw new RuntimeException();
        }
    }

    public int getValue() {
        return value;
    }
}
