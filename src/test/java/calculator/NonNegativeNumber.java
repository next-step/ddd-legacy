package calculator;

class NonNegativeNumber {

    private final int number;

    NonNegativeNumber(final String input) {
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

    int getInt() {
        return this.number;
    }
}
