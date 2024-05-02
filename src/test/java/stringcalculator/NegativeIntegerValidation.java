package stringcalculator;

public class NegativeIntegerValidation {
    public static void checkForNegative(int num) {
        if (num < 0) {
            throw new RuntimeException("Negative integer found: " + num);
        }
    }
}