package stringcalculator;

public class NumberParser {

    public static int parse(String token) {
        try {
            int number = Integer.parseInt(token);
            if (number < 0) {
                throw new RuntimeException("Negative numbers are not allowed: " + number);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid input: " + token);
        }
    }
}
