package calculator;

public class StringCalculator  {

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        if (input.length() == 1) {
            return Integer.parseInt(input);
        }
        return 0;
    }
}
