package calculator;

public class StringCalculator {

    public static int calculate(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        String[] strNumbers = input.split(",|:");
        int sum = 0;
        for (String strNumber : strNumbers) {
            sum += Integer.parseInt(strNumber);
        }

        return sum;
    }
}
