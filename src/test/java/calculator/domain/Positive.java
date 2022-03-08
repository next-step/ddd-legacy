package calculator.domain;

public class Positive {
    private final int number;

    public Positive(int number) {
        if (isNegative(number)) {
            throw new RuntimeException();
        }
        this.number = number;
    }

    private boolean isNegative(final int number) {
        return number < 0;
    }

    public int getNumber() {
        return number;
    }

    public Positive add(Positive positive) {
        return new Positive(this.number + positive.number);
    }
}
