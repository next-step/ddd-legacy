package calculator;

public class StringCalculator {

    public static int calculate(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(input);
    }
}
