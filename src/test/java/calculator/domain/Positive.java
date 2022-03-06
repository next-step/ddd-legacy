package calculator.domain;

public class Positive {
    private final int positive;

    public Positive(int number) {
        if (isNegative(number)) {
            throw new RuntimeException();
        }
        this.positive = number;
    }

    private boolean isNegative(final int number) {
        return number < 0;
    }

    public int getPositive() {
        return positive;
    }
}
