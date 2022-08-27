package calculator;

public class StringAddCalculator {
    public static int splitAndSum(String input) {

        if (!validateInput(input)) {
            return 0;
        }

        int number = Integer.parseInt(input);

        return number;
    }

    private static boolean validateInput(String input) {
        return input != null && !input.isEmpty();
    }

}
