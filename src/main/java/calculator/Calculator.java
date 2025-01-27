package calculator;

public class Calculator {

    public static final String SEPARATOR = "[,;]";

    public int calculate(String input) {
        try {
            if (input == null || input.isEmpty()) {
                return 0;
            }

            String[] splitInput = input.split(SEPARATOR);

            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자의 형태가 아닙니다.");
        }
    }
}
