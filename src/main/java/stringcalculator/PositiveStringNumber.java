package stringcalculator;

public class PositiveStringNumber {

    private final int number;

    public PositiveStringNumber(String stringNumber) {

        try {
            this.number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid integer value: " + stringNumber);
        }

        if (this.number < 0) {
            throw new RuntimeException("negative number is not allowed.");
        }
    }

    public int getNumber() {
        return this.number;
    }
}
