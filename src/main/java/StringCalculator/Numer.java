package StringCalculator;

public class Number {
    private final int value;

    public Number(String text) {
        this.value = Integer.parseInt(text);
        if (this.value < 0) {
            throw new RuntimeException("Negative numbers are not allowed: " + this.value);
        }
    }

    public int getValue() {
        return value;
    }
}
