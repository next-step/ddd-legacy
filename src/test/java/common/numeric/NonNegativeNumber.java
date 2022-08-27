package common.numeric;

public class NonNegativeNumber {

    private final int number;

    public NonNegativeNumber(final String input) {
        int number = parseInt(input);
        if (number < 0) {
            throw new RuntimeException();
        }
        this.number = number;
    }

    private int parseInt(final String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    public int getInt() {
        return this.number;
    }
}
