package stringCalculator;

public class PositiveNumber {
    private int number;

    private PositiveNumber(int number) {
        this.number = number;
    }

    public static PositiveNumber from(String numberString){
        validate(numberString);

        return new PositiveNumber(Integer.parseInt(numberString));
    }

    private static void validate(String numberString) {
        int number = convertStringToNumber(numberString);

        validatePositiveNumber(number);
    }

    private static void validatePositiveNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("numberString included negative number");
        }
    }

    private static int convertStringToNumber(String numberString){
        try {
            return Integer.parseInt(numberString);
        } catch (Exception e) {
            throw new IllegalArgumentException("numberString is invalid value. please input string type number (numberString : "+numberString+")");
        }
    }

    public int getNumber() {
        return number;
    }
}
