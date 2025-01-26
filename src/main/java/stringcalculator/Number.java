package stringcalculator;

public class Number {
    private final int value;

    public Number(String text) {
        this.value = Integer.parseInt(text);
        checkForNegative();
    }

    private void checkForNegative() {
        if (value < 0) {
            throw new RuntimeException("Negative numbers not allowed: " + value);
        }
    }

    public int getValue() {
        return value;
    }
}
