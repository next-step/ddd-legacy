package calculator;

public class StringCalculator {

    private int number;

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        final int addNumber = Integer.parseInt(text);
        return number + addNumber;
    }
}
