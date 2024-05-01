package calculator;

public class StringCalculator {

    public static int calculate(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        String[] strNumbers = input.split(",|:");
        int sum = 0;
        for (String strNumber : strNumbers) {
            sum += toPositive(strNumber);
        }

        return sum;
    }

    private static int toPositive(String strNumber) {
        int number = parseInt(strNumber);
        if (number < 0) {
            throw new RuntimeException("음수는 입력할 수 없습니다.");
        }
        return number;
    }

    private static int parseInt(String strNumber) {
        try {
            return Integer.parseInt(strNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자가 아닌 값은 입력할 수 없습니다.");
        }
    }
}
