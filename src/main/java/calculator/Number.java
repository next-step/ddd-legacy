package calculator;

public class Number {

    private final int value;

    public Number(String text) {
        this.value = Integer.parseInt(text);

        if (this.isPositiveNumber()) {
            throw new RuntimeException();
        }
    }

    public int getValue() {
        return this.value;
    }

    public boolean isPositiveNumber() {
        return this.value < 0;
    }
}
