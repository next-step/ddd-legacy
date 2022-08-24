package stringcalculator;

public class StringCalculator {
    private static final int ZERO = 0;

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return ZERO;
        }
        return Integer.parseInt(text);
    }
}
