package stringcalculator;

public class Positive {
    private final int number;

    public Positive(String text) {
        this.number = Integer.parseInt(text);
        if (this.number < 0) {
            throw new RuntimeException();
        }
    }

    public int parseInt() {
        return number;
    }
}
