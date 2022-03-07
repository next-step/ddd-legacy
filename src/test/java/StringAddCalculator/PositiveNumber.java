package StringAddCalculator;

public class PositiveNumber {

    private static final int ZERO_NUMBER = 0;

    private final String text;

    public PositiveNumber(String text) {
        validatePositiveNumber(text);

        this.text = text;
    }

    private void validatePositiveNumber(String text) {
        if(Integer.parseInt(text) < ZERO_NUMBER) {
            throw new NegativeNumberException();
        }
    }

    public int intValue() {
        return Integer.parseInt(this.text);
    }
}
