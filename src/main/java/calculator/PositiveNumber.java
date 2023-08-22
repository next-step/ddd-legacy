package calculator;

public class PositiveNumber {
    private final int number;

    public PositiveNumber(final int number) {
        if (number < 0) {
            throw new RuntimeException();
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
