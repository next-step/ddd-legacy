package stringCalculator;

public class PositiveNumber {
    private int number;

    private PositiveNumber(int number) {
        this.number = number;
    }

    private static void validateNumberFormatAndPositiveNumber(String numberString) {
        try {
            int num = Integer.parseInt(numberString);

            if (num < 0) {
                throw new RuntimeException("numberString included negative number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("numberString is invalid value. please input string type number (numberString : "+numberString+")");
        }
    }

    public int getNumber() {
        return number;
    }

    public static PositiveNumber from(String numberString){
        validateNumberFormatAndPositiveNumber(numberString);

        return new PositiveNumber(Integer.parseInt(numberString));
    }
}
