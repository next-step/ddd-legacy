package stringCalculator;

public class Number {
    private final int number;

    private Number(final String number) {
        try {
            this.number = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format");
        }
    }

    public static Number from(final String number) {
        return new Number(number);
    }

    public boolean isNegative() {
        return this.number < 0;
    }
    public int getNumber() {
        return number;
    }
}

